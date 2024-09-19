package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Airplane Launcher")
public class AirplaneLauncher extends LinearOpMode {
    private Servo servoLauncher;
    private ServoController ControlHub_ServoController;

    @Override
    public void runOpMode() {
        ControlHub_ServoController = hardwareMap.get(ServoController.class, "Control Hub");
        servoLauncher = hardwareMap.get(Servo.class, "airplane");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (this.gamepad1.right_trigger == 1) {
                servoLauncher.setDirection(Servo.Direction.FORWARD);
                ControlHub_ServoController.pwmEnable();
                servoLauncher.setPosition(0);
                sleep(1000);
                telemetry.addData("Servo", "launched airplane");
                servoLauncher.setDirection(Servo.Direction.REVERSE);
                servoLauncher.setPosition(0);
                sleep(175);
                ControlHub_ServoController.pwmDisable();
                telemetry.addData("Servo", "reset position");
                telemetry.update();
            }
        }
    }
}
