import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.paymentgateway.TicketPaymentService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {

  @Mock
  private TicketPaymentService paymentService;

  private TicketService service;

  @BeforeEach
  public void setup() {
    service = new TicketServiceImpl(paymentService);
  }

  @Test
  @DisplayName("Given a null account ID then throw an exception, with message 'AccountId cannot be null'")
  public void givenNullAccountIdThrowException() {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(null));

    assertEquals("AccountId cannot be null", exception.getMessage(),
        "should return 'AccountId cannot be null'");

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
  }

  @ParameterizedTest
  @CsvSource({"0", "-1", "-100"})
  @DisplayName("Given an account ID is less than or equal to zero then throw an exception, with message 'AccountId must be greater than zero'")
  public void givenAccountIdLessThanZeroThrowException(long input) {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(input));

    assertEquals("AccountId must be greater than zero", exception.getMessage(),
        "should return 'AccountId must be greater than zero'");

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
  }

  @Test
  @DisplayName("Given a null ticketTypeRequests then throw an exception, with message 'Ticket type request cannot be null'")
  public void givenNullTicketTypeRequestThrowException() {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(10L, null));

    assertEquals("Ticket type request cannot be null", exception.getMessage(),
        "should return 'Ticket type request cannot be null'");

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
  }

  @Test
  @DisplayName("Given ticketTypeRequests is empty then throw an exception, with message 'Ticket type request cannot be empty'")
  public void givenEmptyTicketTypeRequestThrowException() {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(10L));

    assertEquals("Ticket type request cannot be empty", exception.getMessage(),
        "should return 'Ticket type request cannot be empty'");

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
  }

  @Test
  @DisplayName("Given ticket size is greater than max (25) then throw an exception 'Maximum ticket size exceeded'")
  public void givenTicketSizeMoreThanMaxThrowException() {

    TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 12);
    TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 10);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 5);

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(10L, adultTicket, childTicket, infantTicket));

    assertEquals("Maximum ticket size exceeded", exception.getMessage(),
        "should return 'Maximum ticket size exceeded'");

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
  }

  @Test
  @DisplayName("Given a child/infant tickets without an adult then throw an exception 'Request must contain an adult ticket'")
  public void givenRequestWithAnAdultThrowException() {

    TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 10);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 5);

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(10L, childTicket, infantTicket));

    assertEquals("Request must contain an adult ticket", exception.getMessage(),
        "should return 'Request must contain an adult ticket'");

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
  }

  @Test
  @DisplayName("Given an infant tickets exceeds an adult tickets then throw an exception 'Infant ticket must not be more than adult ticket'")
  public void givenInfantTicketMoreThanAdultTicketThrowException() {

    TicketTypeRequest childTicket = new TicketTypeRequest(Type.ADULT, 5);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 10);

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(10L, childTicket, infantTicket));

    assertEquals("Infant ticket must not be more than adult ticket", exception.getMessage(),
        "should return 'Infant ticket must not be more than adult ticket'");

    verify(paymentService, never()).makePayment(anyLong(), anyInt());
  }

  @ParameterizedTest
  @CsvSource({"3, 2, 3, 105", "1,0,0, 25", "1, 1, 1, 40"})
  @DisplayName("Given a valid accountId and a valid ticket request, then validate total amount to pay")
  public void givenValidTicketRequestComputeAmount(int adults, int children, int infants,
      int expected) {

    TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, adults);
    TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, children);
    TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, infants);
    service.purchaseTickets(1L, adultTicket, childTicket, infantTicket);

    verify(paymentService, times(1)).makePayment(eq(1L), eq(expected));
  }
}
