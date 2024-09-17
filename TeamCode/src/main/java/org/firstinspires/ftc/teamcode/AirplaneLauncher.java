package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class AirplaneLauncher extends LinearOpMode {
    private Servo servoLauncher;

    @Override
    public void runOpMode() {
        servoLauncher = hardwareMap.get(Servo.class, "airplane");
        servoLauncher.setDirection(Servo.Direction.REVERSE); // change this if needed.

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (this.gamepad1.left_bumper) {
                servoLauncher.setPosition(0);
                sleep(1000);
                servoLauncher.setPosition(0);
                telemetry.addData("Servo", "launched airplane");
            }
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
