import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {


  private TicketService service;

  @BeforeEach
  public void setup() {
    service = new TicketServiceImpl();
  }

  @Test
  @DisplayName("Given a null account ID then throw an exception, with message 'AccountId cannot be null'")
  public void givenNullAccountIdThrowException() {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(null));

    assertEquals("AccountId cannot be null", exception.getMessage(),
        "should return 'AccountId cannot be null'");
  }

  @ParameterizedTest
  @CsvSource({"0", "-1", "-100"})
  @DisplayName("Given an account ID is less than or equal to zero then throw an exception, with message 'AccountId must be greater than zero'")
  public void givenAccountIdLessThanZeroThrowException(long input) {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(input));

    assertEquals("AccountId must be greater than zero", exception.getMessage(),
        "should return 'AccountId must be greater than zero'");
  }

  @Test
  @DisplayName("Given a null ticketTypeRequests then throw an exception, with message 'Ticket type request cannot be null'")
  public void givenNullTicketTypeRequestThrowException() {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(10L, null));

    assertEquals("Ticket type request cannot be null", exception.getMessage(),
        "should return 'Ticket type request cannot be null'");
  }

  @Test
  @DisplayName("Given ticketTypeRequests is empty then throw an exception, with message 'Ticket type request cannot be empty'")
  public void givenEmptyTicketTypeRequestThrowException() {

    InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
        () -> service.purchaseTickets(10L));

    assertEquals("Ticket type request cannot be empty", exception.getMessage(),
        "should return 'Ticket type request cannot be empty'");
  }
}
