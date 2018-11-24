package com.trafficmon;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ChargeSystemTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CongestionChargeSystem chargeSystem = new CongestionChargeSystem();

    @Before
    public void setUpSystemOut() {
        System.setOut(new PrintStream(output));
    }

    @Test
    public void enterBefore2AndStayUpTo4IsChargedFor6() {

    }


    @Test
    public void oldSystemCharges5pEveryMinRoundUp() throws InterruptedException {
        chargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"));
        Thread.sleep(1000);
        chargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"));
        chargeSystem.calculateCharges();
        assertTrue(output.toString().contains(
                "Charge made to account of Fred Bloggs, £0.05 deducted, balance:"));
    }

    @Test
    public void mismatchedEntryExitsTriggerInvestigation() throws InterruptedException {
        chargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"));
        chargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"));
        chargeSystem.calculateCharges();
        assertEquals(output.toString(),
                "Mismatched entries/exits. Triggering investigation into vehicle: Vehicle [A123 XYZ]\r\n");
        output.reset();
        chargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"));
        chargeSystem.calculateCharges();
        assertEquals(output.toString(),
                "Mismatched entries/exits. Triggering investigation into vehicle: Vehicle [A123 XYZ]\r\n");
    }

    @Test
    public void insufficientCreditTriggersPenalty() throws InterruptedException {
        chargeSystem.vehicleEnteringZone(Vehicle.withRegistration("none-exist vehicle"));
        Thread.sleep(1000);
        chargeSystem.vehicleLeavingZone(Vehicle.withRegistration("none-exist vehicle"));
        chargeSystem.calculateCharges();
        assertEquals(output.toString(),
                "Penalty notice for: Vehicle [none-exist vehicle]\r\n");

    }

//    @Test
//    public void checkVehicleEnteringZone(){
//        CongestionChargeSystem ccs = new CongestionChargeSystem();
//        Vehicle vehicle = Vehicle.withRegistration("A123 XYZ");
//        //ccs.vehicleEnteringZone(Vehicle vehicle);
//
//    }
//
//    @Test
//    public void checkVehicleLeavingZone(){
//
//    }
//    public void checkMinutesBetween(){
//        CongestionChargeSystem ccs = new CongestionChargeSystem();
//        assertEquals(ccs.minutesBetween(), 1L);
//    }
}
