package com.emart.parking;

import com.emart.parking.ui.ParkingUI;

/**
 * 이마트 둔산점 주차장 관리 시스템 메인 클래스
 */
public class EmartParkingApplication {

  public static void main(String[] args) {
    // 주차장 관리 UI 생성 및 실행
    ParkingUI ui = new ParkingUI();
    ui.run();
  }
}