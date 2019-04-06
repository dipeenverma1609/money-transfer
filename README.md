# money-transfer
Revolut hiring exercise of a RESTful API (including data model and the backing implementation)
for money transfers between accounts.

For testing money transfer, we'll first we need to create atleast 2 account.
We can create accounts with following currencies only:
1. USD
2. EUR
3. INR
4. YEN
5. SGD

In case, one tries to create account with currency other than above ones, will get an BAD REQUEST status in response.
