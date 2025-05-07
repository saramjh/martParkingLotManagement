package com.emart.parking.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.emart.parking.model.ParkingRecord;

/**
 * 주차 기록을 CSV 파일로 내보내는 유틸리티 클래스
 */
public class CsvExporter {
  private static final String CSV_HEADER = "차량번호,층,주차공간번호,입차시간,출차시간,주차시간(분),주차요금(원)";

  /**
   * 주차 기록을 CSV 파일로 내보냅니다.
   * 
   * @param records  내보낼 주차 기록 목록
   * @param filePath 저장할 파일 경로
   * @return 내보내기 성공 여부
   */
  public boolean exportParkingRecords(List<ParkingRecord> records, String filePath) {
    try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
      // CSV 헤더 작성
      writer.append(CSV_HEADER);
      writer.append(System.lineSeparator());

      // 각 주차 기록을 CSV 형식으로 작성
      for (ParkingRecord record : records) {
        writer.append(record.toCsvString());
        writer.append(System.lineSeparator());
      }

      writer.flush();
      return true;
    } catch (IOException e) {
      System.err.println("CSV 파일 내보내기 오류: " + e.getMessage());
      return false;
    }
  }

  /**
   * 오늘 날짜가 포함된 기본 파일 이름을 생성합니다.
   * 
   * @return "주차기록_YYYY-MM-DD.csv" 형식의 파일 이름
   */
  public static String getDefaultFileName() {
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return "주차기록_" + today.format(formatter) + ".csv";
  }
}