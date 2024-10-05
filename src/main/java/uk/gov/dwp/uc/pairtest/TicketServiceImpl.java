package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;
import java.util.Objects;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketDetailDto;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

  public static final String ACCOUNT_ID_CANNOT_BE_NULL_MESSAGE = "AccountId cannot be null";
  public static final String ACCOUNT_ID_MUST_BE_GRATER_THAN_MESSAGE = "AccountId must be greater than zero";
  public static final String TICKET_TYPE_CANNOT_BE_NULL_MESSAGE = "Ticket type request cannot be null";
  public static final String TICKET_TYPE_CANNOT_BE_EMPTY_MESSAGE = "Ticket type request cannot be empty";
  public static final String TICKET_SIZE_CANNOT_EXCEED_MAX_MESSAGE = "Maximum ticket size exceeded";
  public static final String REQUIRE_ADULT_MESSAGE = "Request must contain an adult ticket";
  public static final String INFANT_TICKET_MORE_THAN_ADULT_TICKET_MESSAGE = "Infant ticket must not be more than adult ticket";
  public static final int MAXIMUM_TICKET_SIZE = 25;
  public static final int COST_PER_ADULT = 25;
  public static final int COST_PER_CHILD = 15;

  private final TicketPaymentService paymentService;
  private final SeatReservationService reservationService;

  public TicketServiceImpl(final TicketPaymentService paymentService,
      SeatReservationService reservationService) {
    this.paymentService = paymentService;
    this.reservationService = reservationService;
  }

  /**
   * Should only have private methods other than the one below.
   */

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
      throws InvalidPurchaseException {

    validateAccountId(accountId);
    validateTicketRequest(ticketTypeRequests);

    final TicketDetailDto ticketDetails = getTicketDetails(ticketTypeRequests);

    validateBusinessRules(ticketDetails);

    int totalTicketPrice = computeTotalTicketPrice(ticketDetails);
    int totalSeatsToReserve = ticketDetails.noOfAdults() + ticketDetails.noOfChildren();

    paymentService.makePayment(accountId, totalTicketPrice);
    reservationService.reserveSeat(accountId, totalSeatsToReserve);
  }

  private static TicketDetailDto getTicketDetails(final TicketTypeRequest... ticketTypeRequests) {
    int noOfAdults = Arrays.stream(ticketTypeRequests)
        .filter(ticket -> ticket.getTicketType() == Type.ADULT)
        .mapToInt(TicketTypeRequest::getNoOfTickets)
        .sum();

    int noOfInfants = Arrays.stream(ticketTypeRequests)
        .filter(ticket -> ticket.getTicketType() == Type.INFANT)
        .mapToInt(TicketTypeRequest::getNoOfTickets)
        .sum();

    int noOfChildren = Arrays.stream(ticketTypeRequests)
        .filter(ticket -> ticket.getTicketType() == Type.CHILD)
        .mapToInt(TicketTypeRequest::getNoOfTickets)
        .sum();

    return new TicketDetailDto(noOfAdults, noOfInfants, noOfChildren);
  }

  private static int computeTotalTicketPrice(final TicketDetailDto ticketDetailDto) {
    return (ticketDetailDto.noOfAdults() * COST_PER_ADULT) + (ticketDetailDto.noOfChildren()
        * COST_PER_CHILD);
  }

  private static void validateBusinessRules(final TicketDetailDto ticketDetails) {
    if (ticketDetails.noOfAdults() < 1) {
      throw new InvalidPurchaseException(REQUIRE_ADULT_MESSAGE);
    }

    if (ticketDetails.noOfInfants() > ticketDetails.noOfAdults()) {
      throw new InvalidPurchaseException(INFANT_TICKET_MORE_THAN_ADULT_TICKET_MESSAGE);
    }

    if ((ticketDetails.noOfAdults() + ticketDetails.noOfInfants() + ticketDetails.noOfChildren())
        > MAXIMUM_TICKET_SIZE) {
      throw new InvalidPurchaseException(TICKET_SIZE_CANNOT_EXCEED_MAX_MESSAGE);
    }
  }

  private void validateTicketRequest(final TicketTypeRequest... ticketTypeRequests) {
    if (Objects.isNull(ticketTypeRequests)) {
      throw new InvalidPurchaseException(TICKET_TYPE_CANNOT_BE_NULL_MESSAGE);
    } else if (ticketTypeRequests.length < 1) {
      throw new InvalidPurchaseException(TICKET_TYPE_CANNOT_BE_EMPTY_MESSAGE);
    }

    int totalNoOfTickets = Arrays.stream(ticketTypeRequests)
        .mapToInt(TicketTypeRequest::getNoOfTickets)
        .sum();

    if (totalNoOfTickets > MAXIMUM_TICKET_SIZE) {
      throw new InvalidPurchaseException(TICKET_SIZE_CANNOT_EXCEED_MAX_MESSAGE);
    }
  }

  private void validateAccountId(final Long accountId) {

    if (Objects.isNull(accountId)) {
      throw new InvalidPurchaseException(ACCOUNT_ID_CANNOT_BE_NULL_MESSAGE);
    } else if (accountId < 1) {
      throw new InvalidPurchaseException(ACCOUNT_ID_MUST_BE_GRATER_THAN_MESSAGE);
    }
  }
}
