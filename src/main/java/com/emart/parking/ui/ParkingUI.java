package com.emart.parking.ui;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.emart.parking.model.ParkingRecord;
import com.emart.parking.model.ParkingSpace;
import com.emart.parking.service.ParkingService;
import com.emart.parking.util.CsvExporter;

/**
 * 주차장 관리 시스템의 콘솔 기반 사용자 인터페이스
 */
public class ParkingUI {
  private final ParkingService parkingService;
  private final Scanner scanner;

  public ParkingUI() {
    this.parkingService = new ParkingService();
    this.scanner = new Scanner(System.in);
  }

  /**
   * 애플리케이션 실행
   */
  public void run() {
    System.out.println("==============================================");
    System.out.println("    이마트 둔산점 주차장 관리 시스템 v1.0");
    System.out.println("==============================================");

    boolean running = true;
    while (running) {
      displayMainMenu();
      int choice = getUserChoice(1, 7);

      switch (choice) {
        case 1:
          showParkingStatus();
          break;
        case 2:
          handleCarEntry();
          break;
        case 3:
          handleCarExit();
          break;
        case 4:
          showParkingRecords();
          break;
        case 5:
          exportParkingRecords();
          break;
        case 6:
          showRevenueInfo();
          break;
        case 7:
          running = false;
          System.out.println("프로그램을 종료합니다.");
          break;
      }
    }
    scanner.close();
  }

  /**
   * 메인 메뉴 표시
   */
  private void displayMainMenu() {
    System.out.println("\n[메인 메뉴]");
    System.out.println("1. 주차장 현황 조회");
    System.out.println("2. 차량 입차 처리");
    System.out.println("3. 차량 출차 처리");
    System.out.println("4. 주차 기록 조회");
    System.out.println("5. 주차 기록 내보내기 (CSV)");
    System.out.println("6. 매출 현황 조회");
    System.out.println("7. 종료");
    System.out.print("메뉴를 선택하세요: ");
  }

  /**
   * 사용자 입력 받기 (범위 제한 있음)
   */
  private int getUserChoice(int min, int max) {
    int choice = -1;
    while (choice < min || choice > max) {
      try {
        choice = Integer.parseInt(scanner.nextLine().trim());
        if (choice < min || choice > max) {
          System.out.print("잘못된 입력입니다. 다시 입력하세요: ");
        }
      } catch (NumberFormatException e) {
        System.out.print("숫자를 입력하세요: ");
      }
    }
    return choice;
  }

  /**
   * 주차장 현황 조회
   */
  private void showParkingStatus() {
    System.out.println("\n[주차장 현황]");

    // 전체 주차장 통계
    Map<String, Integer> totalStats = parkingService.getTotalStatistics();
    System.out.printf("전체 주차 공간: %d, 사용 중: %d, 비어있음: %d\n",
        totalStats.get("전체"), totalStats.get("사용중"), totalStats.get("비어있음"));

    // 층별 주차장 통계
    Map<Integer, Map<String, Integer>> floorStats = parkingService.getStatisticsByFloor();
    System.out.println("\n[층별 주차 현황]");
    for (Integer floor : parkingService.getFloorList()) {
      Map<String, Integer> stats = floorStats.get(floor);
      System.out.printf("%d층: 전체 %d, 사용 중 %d, 비어있음 %d\n",
          floor, stats.get("전체"), stats.get("사용중"), stats.get("비어있음"));
    }

    // 세부 주차 공간 현황
    System.out.println("\n상세 주차 공간 현황을 확인하시겠습니까? (Y/N): ");
    String answer = scanner.nextLine().trim().toUpperCase();
    if (answer.equals("Y")) {
      System.out.print("확인할 층을 입력하세요: ");
      try {
        int floor = Integer.parseInt(scanner.nextLine().trim());
        displayFloorDetails(floor);
      } catch (NumberFormatException e) {
        System.out.println("잘못된 입력입니다.");
      }
    }
  }

  /**
   * 특정 층의 주차 공간 세부 정보 표시
   */
  private void displayFloorDetails(int floor) {
    List<ParkingSpace> spaces = parkingService.getParkingSpacesByFloor(floor);
    if (spaces.isEmpty()) {
      System.out.println("해당 층의 주차 공간이 없습니다.");
      return;
    }

    System.out.printf("\n[%d층 주차 공간 현황]\n", floor);
    for (ParkingSpace space : spaces) {
      String status = space.isOccupied() ? String.format("점유 (차량: %s, 입차시간: %s)",
          space.getCarNumber(),
          space.getParkingStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) : "비어있음";
      System.out.printf("공간 %03d: %s\n", space.getSpaceNumber(), status);
    }
  }

  /**
   * 차량 입차 처리
   */
  private void handleCarEntry() {
    System.out.println("\n[차량 입차 처리]");

    System.out.print("입차할 층 번호를 입력하세요: ");
    int floor = getValidatedFloor();
    if (floor == -1)
      return;

    System.out.print("입차할 주차 공간 번호를 입력하세요: ");
    int spaceNumber = getValidatedSpaceNumber(floor);
    if (spaceNumber == -1)
      return;

    // 해당 주차 공간이 이미 점유 중인지 확인
    ParkingSpace space = parkingService.getParkingSpace(floor, spaceNumber);
    if (space.isOccupied()) {
      System.out.println("해당 주차 공간은 이미 점유 중입니다. (차량번호: " + space.getCarNumber() + ")");
      return;
    }

    System.out.print("차량 번호를 입력하세요 (예: 12가3456): ");
    String carNumber = scanner.nextLine().trim();
    if (carNumber.isEmpty()) {
      System.out.println("차량 번호는 필수 입력 사항입니다.");
      return;
    }

    boolean success = parkingService.parkCar(floor, spaceNumber, carNumber);
    if (success) {
      System.out.printf("%d층 %d번 주차 공간에 차량(%s)이 입차되었습니다.\n", floor, spaceNumber, carNumber);
    } else {
      System.out.println("차량 입차 처리 중 오류가 발생했습니다.");
    }
  }

  /**
   * 차량 출차 처리
   */
  private void handleCarExit() {
    System.out.println("\n[차량 출차 처리]");

    System.out.print("출차할 층 번호를 입력하세요: ");
    int floor = getValidatedFloor();
    if (floor == -1)
      return;

    System.out.print("출차할 주차 공간 번호를 입력하세요: ");
    int spaceNumber = getValidatedSpaceNumber(floor);
    if (spaceNumber == -1)
      return;

    // 해당 주차 공간에 차량이 있는지 확인
    ParkingSpace space = parkingService.getParkingSpace(floor, spaceNumber);
    if (!space.isOccupied()) {
      System.out.println("해당 주차 공간에 주차된 차량이 없습니다.");
      return;
    }

    System.out.printf("차량(%s)을 출차 처리하시겠습니까? (Y/N): ", space.getCarNumber());
    String confirm = scanner.nextLine().trim().toUpperCase();
    if (!confirm.equals("Y")) {
      System.out.println("출차 처리를 취소합니다.");
      return;
    }

    ParkingRecord record = parkingService.exitCar(floor, spaceNumber);
    if (record != null) {
      System.out.println("\n[출차 내역]");
      System.out.println("차량번호: " + record.getCarNumber());
      System.out.println("주차 위치: " + record.getFloor() + "층 " + record.getSpaceNumber() + "번");
      System.out.println("입차 시간: " + record.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      System.out.println("출차 시간: " + record.getExitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      System.out.println("주차 시간: " + record.getParkingDuration() + "분");
      System.out.println("주차 요금: " + String.format("%,d", record.getParkingFee()) + "원");
      System.out.println("\n차량이 성공적으로 출차 처리되었습니다.");
    } else {
      System.out.println("차량 출차 처리 중 오류가 발생했습니다.");
    }
  }

  /**
   * 입력된 층 번호가 유효한지 검증
   */
  private int getValidatedFloor() {
    try {
      int floor = Integer.parseInt(scanner.nextLine().trim());
      if (!parkingService.getFloorList().contains(floor)) {
        System.out.println("유효하지 않은 층 번호입니다. 층 번호는 " +
            parkingService.getFloorList().toString() + " 중에 선택해주세요.");
        return -1;
      }
      return floor;
    } catch (NumberFormatException e) {
      System.out.println("유효한 숫자를 입력하세요.");
      return -1;
    }
  }

  /**
   * 입력된 주차 공간 번호가 유효한지 검증
   */
  private int getValidatedSpaceNumber(int floor) {
    try {
      int spaceNumber = Integer.parseInt(scanner.nextLine().trim());
      ParkingSpace space = parkingService.getParkingSpace(floor, spaceNumber);
      if (space == null) {
        List<ParkingSpace> spaces = parkingService.getParkingSpacesByFloor(floor);
        int maxNumber = spaces.isEmpty() ? 0 : spaces.size();
        System.out.println("유효하지 않은 주차 공간 번호입니다. 1에서 " + maxNumber + " 사이의 값을 입력하세요.");
        return -1;
      }
      return spaceNumber;
    } catch (NumberFormatException e) {
      System.out.println("유효한 숫자를 입력하세요.");
      return -1;
    }
  }

  /**
   * 주차 기록 조회
   */
  private void showParkingRecords() {
    System.out.println("\n[주차 기록 조회]");
    List<ParkingRecord> records = parkingService.getParkingRecords();

    if (records.isEmpty()) {
      System.out.println("주차 기록이 없습니다.");
      return;
    }

    // 주차 기록 헤더
    System.out.println("번호\t차량번호\t위치\t\t입차시간\t\t\t출차시간\t\t\t시간(분)\t요금(원)");
    System.out
        .println("----------------------------------------------------------------------------------------------");

    // 각 주차 기록 출력
    int index = 1;
    for (ParkingRecord record : records) {
      String entryTime = record.getEntryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      String exitTime = record.getExitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      System.out.printf("%d\t%s\t%d층-%03d\t%s\t%s\t%d\t%,d\n",
          index++, record.getCarNumber(), record.getFloor(), record.getSpaceNumber(),
          entryTime, exitTime, record.getParkingDuration(), record.getParkingFee());
    }
  }

  /**
   * 주차 기록 CSV 파일로 내보내기
   */
  private void exportParkingRecords() {
    System.out.println("\n[주차 기록 내보내기]");
    List<ParkingRecord> records = parkingService.getParkingRecords();

    if (records.isEmpty()) {
      System.out.println("내보낼 주차 기록이 없습니다.");
      return;
    }

    String defaultFileName = CsvExporter.getDefaultFileName();
    System.out.print("파일 이름을 입력하세요 (기본값: " + defaultFileName + "): ");
    String fileName = scanner.nextLine().trim();

    if (fileName.isEmpty()) {
      fileName = defaultFileName;
    }

    String filePath = Paths.get(System.getProperty("user.dir"), fileName).toString();
    boolean success = parkingService.exportParkingRecordsToCsv(filePath);

    if (success) {
      System.out.println("주차 기록이 성공적으로 내보내졌습니다: " + filePath);
    } else {
      System.out.println("주차 기록 내보내기에 실패했습니다.");
    }
  }

  /**
   * 매출 정보 조회
   */
  private void showRevenueInfo() {
    System.out.println("\n[매출 현황 조회]");

    LocalDate today = LocalDate.now();
    int todayRevenue = parkingService.getDailyRevenue(today);

    System.out.println("오늘 (" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ") 매출: " +
        String.format("%,d원", todayRevenue));

    // 다른 날짜의 매출도 조회할 수 있도록 기능 제공
    System.out.print("\n다른 날짜의 매출을 조회하시겠습니까? (Y/N): ");
    if (scanner.nextLine().trim().toUpperCase().equals("Y")) {
      System.out.print("조회할 날짜를 입력하세요 (YYYY-MM-DD): ");
      String dateStr = scanner.nextLine().trim();

      try {
        LocalDate date = LocalDate.parse(dateStr);
        int revenue = parkingService.getDailyRevenue(date);
        System.out.println(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 매출: " +
            String.format("%,d원", revenue));
      } catch (Exception e) {
        System.out.println("유효한 날짜 형식이 아닙니다. (YYYY-MM-DD 형식으로 입력하세요)");
      }
    }
  }
}