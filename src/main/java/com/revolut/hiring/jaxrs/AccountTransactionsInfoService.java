package com.revolut.hiring.jaxrs;

import com.revolut.hiring.bean.BankAccountInfo;
import com.revolut.hiring.bean.BankAccountTransactionInfo;
import com.revolut.hiring.dao.AccountTxnDataService;
import com.revolut.hiring.jaxrs.bean.AccountTransaction;
import com.revolut.hiring.service.BankAccountService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static com.revolut.hiring.util.DateUtil.getDate;

@Path("account/txn")
public class AccountTransactionsInfoService {

    private static final String QUERY_PARAM_DATE_FORMAT = "dd-MM-yyyy";

    private final BankAccountService accountService = new BankAccountService();
    private final AccountTxnDataService txnDataService = AccountTxnDataService.getInstance();

    @GET
    @Path("{acntid: [0-9]+}/{txnid: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTxnDetails(@PathParam("acntid") long accountId, @PathParam("txnid") long txnId) {

        final BankAccountInfo accountInfo = accountService.getAccountInfo(accountId);
        if (accountInfo != null) {
            final List<BankAccountTransactionInfo> accntTxns = txnDataService.getAllTransactions(accountId);
            for(BankAccountTransactionInfo accntTxn : accntTxns) {
                if (txnId == accntTxn.getId()) {
                    AccountTransaction txn = new AccountTransaction(accntTxn);
                    return Response.ok().entity(txn).build();
                }
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{id: [0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTxnsDetails(@PathParam("id") String accntId,
                                   @QueryParam("from") String fromDate, @QueryParam("to") String endDate) {

        List<AccountTransaction> txns = new LinkedList<>();
        if (accntId == null || accntId.isEmpty()) return Response.status(Response.Status.BAD_REQUEST).build();

        final long accountId = Long.parseLong(accntId);

        if (fromDate == null && endDate == null) {
            txns.addAll(getTxnDetails(accountId).stream().map(AccountTransaction::new).collect(Collectors.toList()));

        } else if (fromDate != null) {

            final List<BankAccountTransactionInfo> txnDetails = (endDate == null) ?
                    getTxnDetails(accountId, getDate(fromDate,QUERY_PARAM_DATE_FORMAT)) :
                    getTxnDetails(accountId, getDate(fromDate,QUERY_PARAM_DATE_FORMAT), getDate(endDate,QUERY_PARAM_DATE_FORMAT));

            txns.addAll(txnDetails.stream().map(AccountTransaction::new).collect(Collectors.toList()));
        }

        if (txns.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(txns).build();
    }

    private List<BankAccountTransactionInfo> getTxnDetails(long accountId, Date fromDate, Date endDate) {
        final BankAccountInfo accountInfo = accountService.getAccountInfo(accountId);
        if (accountInfo != null) {
            return txnDataService.getAllTransactions(fromDate, endDate);
        }
        return Collections.emptyList();
    }

    private List<BankAccountTransactionInfo> getTxnDetails(long accountId) {
        final BankAccountInfo accountInfo = accountService.getAccountInfo(accountId);
        if (accountInfo != null) {
            return txnDataService.getAllTransactions(accountId);
        }
        return Collections.emptyList();
    }

    private List<BankAccountTransactionInfo> getTxnDetails(long accountId, Date fromDate) {
        final BankAccountInfo accountInfo = accountService.getAccountInfo(accountId);
        if (accountInfo != null) {
            return txnDataService.getAllTransactions(fromDate);
        }
        return Collections.emptyList();
    }
}
