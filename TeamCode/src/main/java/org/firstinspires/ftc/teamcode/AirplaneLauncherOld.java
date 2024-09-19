package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

@TeleOp(name = "_23983 (Blocks to Java)")
public class AirplaneLauncherOld extends LinearOpMode {

    private ServoController ControlHub_ServoController;
    private Servo airplane;

    /**
     * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
     * Comment Blocks show where to place Initialization code (runs once, after touching the
     * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
     * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
     * Stopped).
     */
    @Override
    public void runOpMode() {
        ControlHub_ServoController = hardwareMap.get(ServoController.class, "Control Hub");
        airplane = hardwareMap.get(Servo.class, "airplane");

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.circle) {
                ControlHub_ServoController.pwmEnable();
                airplane.setDirection(Servo.Direction.REVERSE);
                airplane.setPosition(1);
            } else if (gamepad1.triangle) {
                ControlHub_ServoController.pwmEnable();
                airplane.setDirection(Servo.Direction.FORWARD);
                airplane.setPosition(1);
            } else if (gamepad1.square) {
                ControlHub_ServoController.pwmDisable();
            }
            telemetry.update();
        }
    }
}