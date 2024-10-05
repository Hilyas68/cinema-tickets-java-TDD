package uk.gov.dwp.uc.pairtest.domain;

public record TicketDetailDto(int noOfAdults, int noOfInfants, int noOfChildren) {

  public int getTotalTicketCount() {
    return noOfAdults + noOfChildren + noOfInfants;
  }
}