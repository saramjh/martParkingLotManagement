package com.emart.parking.model;

import java.time.LocalDateTime;

/**
 * 개별 주차 공간을 나타내는 클래스
 */
public class ParkingSpace {
  private final int floor; // 층 번호
  private final int spaceNumber; // 주차 공간 번호
  private boolean occupied; // 주차 공간 점유 여부
  private String carNumber; // 주차된 차량 번호
  private LocalDateTime parkingStartTime; // 주차 시작 시간

  public ParkingSpace(int floor, int spaceNumber) {
    this.floor = floor;
    this.spaceNumber = spaceNumber;
    this.occupied = false;
    this.carNumber = null;
    this.parkingStartTime = null;
  }

  // 차량 입차 처리
  public void parkCar(String carNumber) {
    this.occupied = true;
    this.carNumber = carNumber;
    this.parkingStartTime = LocalDateTime.now();
  }

  // 차량 출차 처리
  public ParkingRecord removeParkedCar() {
    if (!this.occupied) {
      return null;
    }

    LocalDateTime exitTime = LocalDateTime.now();
    ParkingRecord record = new ParkingRecord(
        this.carNumber,
        this.floor,
        this.spaceNumber,
        this.parkingStartTime,
        exitTime);

    this.occupied = false;
    this.carNumber = null;
    this.parkingStartTime = null;

    return record;
  }

  // Getter 및 Setter 메서드
  public int getFloor() {
    return floor;
  }

  public int getSpaceNumber() {
    return spaceNumber;
  }

  public boolean isOccupied() {
    return occupied;
  }

  public String getCarNumber() {
    return carNumber;
  }

  public LocalDateTime getParkingStartTime() {
    return parkingStartTime;
  }

  @Override
  public String toString() {
    if (occupied) {
      return String.format("[%d층-%03d] 점유 (차량번호: %s, 입차시간: %s)",
          floor, spaceNumber, carNumber, parkingStartTime);
    } else {
      return String.format("[%d층-%03d] 비어있음", floor, spaceNumber);
    }
  }
}