package uk.gov.dwp.uc.pairtest;

import java.util.Objects;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    public static final String ACCOUNT_ID_CANNOT_BE_NULL_MESSAGE = "AccountId cannot be null";

    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if(Objects.isNull(accountId)){
            throw new InvalidPurchaseException(ACCOUNT_ID_CANNOT_BE_NULL_MESSAGE);
        }
    }

}
