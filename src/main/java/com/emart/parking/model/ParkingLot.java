package com.emart.parking.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 이마트 둔산점 주차장을 나타내는 클래스
 */
public class ParkingLot {
  private final Map<Integer, List<ParkingSpace>> parkingSpacesByFloor; // 층별 주차 공간
  private final List<ParkingRecord> parkingRecords; // 주차 기록
  private final Map<LocalDate, Integer> dailyRevenue; // 날짜별 매출

  // 층별 주차 자리 수
  private static final Map<Integer, Integer> SPACES_PER_FLOOR = new HashMap<>();
  static {
    SPACES_PER_FLOOR.put(4, 20); // 4층 20자리
    SPACES_PER_FLOOR.put(5, 25); // 5층 25자리
    SPACES_PER_FLOOR.put(6, 25); // 6층 25자리
  }

  public ParkingLot() {
    this.parkingSpacesByFloor = new HashMap<>();
    this.parkingRecords = new ArrayList<>();
    this.dailyRevenue = new HashMap<>();

    initializeParkingSpaces();
  }

  // 주차 공간 초기화
  private void initializeParkingSpaces() {
    for (Map.Entry<Integer, Integer> entry : SPACES_PER_FLOOR.entrySet()) {
      int floor = entry.getKey();
      int spaceCount = entry.getValue();

      List<ParkingSpace> spaces = new ArrayList<>();
      for (int i = 1; i <= spaceCount; i++) {
        spaces.add(new ParkingSpace(floor, i));
      }

      parkingSpacesByFloor.put(floor, spaces);
    }
  }

  // 특정 주차 공간 찾기
  public ParkingSpace getParkingSpace(int floor, int spaceNumber) {
    List<ParkingSpace> spaces = parkingSpacesByFloor.get(floor);
    if (spaces == null) {
      return null; // 해당 층이 없음
    }

    for (ParkingSpace space : spaces) {
      if (space.getSpaceNumber() == spaceNumber) {
        return space;
      }
    }

    return null; // 해당 번호의 주차 공간이 없음
  }

  // 입차 처리
  public boolean parkCar(int floor, int spaceNumber, String carNumber) {
    ParkingSpace space = getParkingSpace(floor, spaceNumber);
    if (space == null || space.isOccupied()) {
      return false;
    }

    space.parkCar(carNumber);
    return true;
  }

  // 출차 처리
  public ParkingRecord exitCar(int floor, int spaceNumber) {
    ParkingSpace space = getParkingSpace(floor, spaceNumber);
    if (space == null || !space.isOccupied()) {
      return null;
    }

    ParkingRecord record = space.removeParkedCar();
    if (record != null) {
      parkingRecords.add(record);

      // 일일 매출 업데이트
      LocalDate today = LocalDate.now();
      dailyRevenue.put(today, dailyRevenue.getOrDefault(today, 0) + record.getParkingFee());
    }

    return record;
  }

  // 층별 사용 현황 통계
  public Map<Integer, Map<String, Integer>> getStatisticsByFloor() {
    Map<Integer, Map<String, Integer>> statistics = new HashMap<>();

    for (int floor : parkingSpacesByFloor.keySet()) {
      List<ParkingSpace> spaces = parkingSpacesByFloor.get(floor);
      int total = spaces.size();
      int occupied = 0;

      for (ParkingSpace space : spaces) {
        if (space.isOccupied()) {
          occupied++;
        }
      }

      Map<String, Integer> floorStats = new HashMap<>();
      floorStats.put("전체", total);
      floorStats.put("사용중", occupied);
      floorStats.put("비어있음", total - occupied);

      statistics.put(floor, floorStats);
    }

    return statistics;
  }

  // 전체 주차장 사용 현황 통계
  public Map<String, Integer> getTotalStatistics() {
    int totalSpaces = 0;
    int occupiedSpaces = 0;

    for (List<ParkingSpace> spaces : parkingSpacesByFloor.values()) {
      totalSpaces += spaces.size();

      for (ParkingSpace space : spaces) {
        if (space.isOccupied()) {
          occupiedSpaces++;
        }
      }
    }

    Map<String, Integer> statistics = new HashMap<>();
    statistics.put("전체", totalSpaces);
    statistics.put("사용중", occupiedSpaces);
    statistics.put("비어있음", totalSpaces - occupiedSpaces);

    return statistics;
  }

  // 특정 일자의 매출 조회
  public int getDailyRevenue(LocalDate date) {
    return dailyRevenue.getOrDefault(date, 0);
  }

  // 층별 주차 공간 목록 반환
  public List<ParkingSpace> getParkingSpacesByFloor(int floor) {
    return parkingSpacesByFloor.getOrDefault(floor, new ArrayList<>());
  }

  // 전체 층 목록 반환
  public List<Integer> getFloorList() {
    return new ArrayList<>(parkingSpacesByFloor.keySet());
  }

  // 주차 기록 반환
  public List<ParkingRecord> getParkingRecords() {
    return new ArrayList<>(parkingRecords);
  }
}