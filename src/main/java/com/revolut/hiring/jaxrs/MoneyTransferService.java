package com.revolut.hiring.jaxrs;

import com.revolut.hiring.exceptions.InsufficientFundsException;
import com.revolut.hiring.jaxrs.bean.*;
import com.revolut.hiring.service.AccountTransactionService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.Date;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static com.revolut.hiring.util.DateUtil.getDateString;

@Path("account/money")
public class MoneyTransferService {

    private final AccountTransactionService txnService = new AccountTransactionService();

    @POST
    @Path("/credit")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response credit(AccountCreditRequest request) {
        AccountCreditResponse entity = new AccountCreditResponse();
        entity.setTime(getDateString(new Date()));
        entity.setRequest(request);

        if (!isAmountValid(request.getAmount())) {
            entity.setStatus("Invalid Amount in Request");
            return Response.status(BAD_REQUEST).entity(entity).build();
        }

        try {
            long txnId = txnService.credit(request.getAccount(), request.getAmount());
            entity.setTxnId(txnId);
            entity.setStatus("Success");
        } catch (IllegalArgumentException cause) {
            entity.setStatus(cause.getMessage());
            return Response.status(BAD_REQUEST).entity(entity).build();

        } catch (Throwable cause) {
            entity.setStatus("Failed :: " + cause.getMessage());
            return Response.serverError().entity(entity).build();
        }

        return Response.ok().entity(entity).build();
    }

    @POST
    @Path("/debit")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response debit(AccountDebitRequest request) {
        AccountDebitResponse entity = new AccountDebitResponse();
        entity.setTime(getDateString(new Date()));
        entity.setRequest(request);

        if (!isAmountValid(request.getAmount())) {
            entity.setStatus("Invalid Amount in Request");
            return Response.status(BAD_REQUEST).entity(entity).build();
        }

        try {
            long txnId = txnService.debit(request.getAccount(), request.getAmount());
            entity.setTxnId(txnId);
        } catch (InsufficientFundsException cause) {
            entity.setStatus(cause.getMessage());
            return Response.status(FORBIDDEN).entity(entity).build();

        } catch (IllegalArgumentException cause) {
            entity.setStatus(cause.getMessage());
            return Response.status(BAD_REQUEST).entity(entity).build();

        } catch (Throwable cause) {
            entity.setStatus("Failed :: " + cause.getMessage());
            return Response.serverError().entity(entity).build();
        }

        return Response.ok().entity(entity).build();
    }

    @POST
    @Path("/transfer")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public Response transfer(final MoneyTransferRequest request) {
        final MoneyTransferResponse entity = new MoneyTransferResponse();
        entity.setRequest(request);
        entity.setTime(getDateString(new Date()));

        if (request.getFromAccount() == request.getToAccount()) {
            entity.setStatus("Both Accounts are same");
            return Response.status(BAD_REQUEST).entity(entity).build();
        }
        if (!isAmountValid(request.getAmount())) {
            entity.setStatus("Invalid Amount in Request");
            return Response.status(BAD_REQUEST).entity(entity).build();
        }

        try {
            long txnId = txnService.transfer(request.getFromAccount(), request.getToAccount(), request.getAmount());
            entity.setTxnId(txnId);
            entity.setStatus("Success");

        } catch (InsufficientFundsException cause) {
            entity.setStatus(cause.getMessage());
            return Response.status(FORBIDDEN).entity(entity).build();

        } catch (IllegalArgumentException cause) {
            entity.setStatus(cause.getMessage());
            return Response.status(BAD_REQUEST).entity(entity).build();

        } catch (Throwable cause) {
            entity.setStatus("Failed :: " + cause.getMessage());
            return Response.serverError().entity(entity).build();
        }

        return Response.status(ACCEPTED).entity(entity).build();
    }

    private boolean isAmountValid(Double amount) {
        boolean isValid = true;

        if (amount == null || amount.isNaN()) isValid &= false;

        return isValid;
    }
}
