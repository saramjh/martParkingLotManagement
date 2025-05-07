package com.emart.parking.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 주차 기록을 나타내는 클래스
 */
public class ParkingRecord {
  private final String carNumber; // 차량 번호
  private final int floor; // 주차 층
  private final int spaceNumber; // 주차 자리 번호
  private final LocalDateTime entryTime; // 입차 시간
  private final LocalDateTime exitTime; // 출차 시간
  private final long parkingDuration; // 주차 시간(분)
  private final int parkingFee; // 주차 요금(원)

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public ParkingRecord(String carNumber, int floor, int spaceNumber,
      LocalDateTime entryTime, LocalDateTime exitTime) {
    this.carNumber = carNumber;
    this.floor = floor;
    this.spaceNumber = spaceNumber;
    this.entryTime = entryTime;
    this.exitTime = exitTime;
    this.parkingDuration = calculateParkingDuration();
    this.parkingFee = calculateParkingFee();
  }

  // 주차 시간 계산 (분 단위)
  private long calculateParkingDuration() {
    return Duration.between(entryTime, exitTime).toMinutes();
  }

  // 주차 요금 계산
  private int calculateParkingFee() {
    // 최초 30분까지 2,000원
    // 10분마다 500원씩 추가 (30분 이후부터)
    int fee = 2000; // 기본 요금

    if (parkingDuration > 30) {
      // 30분을 초과한 시간에 대해 10분당 500원 추가
      // 10분으로 나누어 떨어지지 않는 경우 올림 처리
      long additionalUnits = (parkingDuration - 30 + 9) / 10; // 올림 계산
      fee += additionalUnits * 500;
    }

    return fee;
  }

  // CSV 형식으로 데이터 반환
  public String toCsvString() {
    return String.format("%s,%d,%d,%s,%s,%d,%d",
        carNumber, floor, spaceNumber,
        entryTime.format(formatter), exitTime.format(formatter),
        parkingDuration, parkingFee);
  }

  // Getter 메서드
  public String getCarNumber() {
    return carNumber;
  }

  public int getFloor() {
    return floor;
  }

  public int getSpaceNumber() {
    return spaceNumber;
  }

  public LocalDateTime getEntryTime() {
    return entryTime;
  }

  public LocalDateTime getExitTime() {
    return exitTime;
  }

  public long getParkingDuration() {
    return parkingDuration;
  }

  public int getParkingFee() {
    return parkingFee;
  }

  @Override
  public String toString() {
    return String.format("차량번호: %s | 위치: %d층-%03d | 입차: %s | 출차: %s | 주차시간: %d분 | 요금: %,d원",
        carNumber, floor, spaceNumber,
        entryTime.format(formatter), exitTime.format(formatter),
        parkingDuration, parkingFee);
  }
}