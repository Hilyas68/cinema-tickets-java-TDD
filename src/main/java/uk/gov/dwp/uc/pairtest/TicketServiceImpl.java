package uk.gov.dwp.uc.pairtest;

import java.util.Objects;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

  public static final String ACCOUNT_ID_CANNOT_BE_NULL_MESSAGE = "AccountId cannot be null";
  public static final String ACCOUNT_ID_MUST_BE_GRATER_THAN_MESSAGE = "AccountId must be greater than zero";
  public static final String TICKET_TYPE_CANNOT_BE_NULL_MESSAGE = "Ticket type request cannot be null";
  public static final String TICKET_TYPE_CANNOT_BE_EMPTY_MESSAGE = "Ticket type request cannot be empty";


  /**
   * Should only have private methods other than the one below.
   */

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
      throws InvalidPurchaseException {
    if (Objects.isNull(accountId)) {
      throw new InvalidPurchaseException(ACCOUNT_ID_CANNOT_BE_NULL_MESSAGE);
    } else if (accountId < 1) {
      throw new InvalidPurchaseException(ACCOUNT_ID_MUST_BE_GRATER_THAN_MESSAGE);
    }

    validateTicketRequest(ticketTypeRequests);
  }

  private void validateTicketRequest(final TicketTypeRequest... ticketTypeRequests) {
    if (Objects.isNull(ticketTypeRequests)) {
      throw new InvalidPurchaseException(TICKET_TYPE_CANNOT_BE_NULL_MESSAGE);
    } else if (ticketTypeRequests.length < 1) {
      throw new InvalidPurchaseException(TICKET_TYPE_CANNOT_BE_EMPTY_MESSAGE);
    }
  }
}
