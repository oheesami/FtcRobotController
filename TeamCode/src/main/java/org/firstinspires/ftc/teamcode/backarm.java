package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Back Arm Control", group = "TeleOp")
public class backarm extends OpMode {

    private CRServo armServo; // Continuous rotation servo for rotating the arm
    private Servo clawMotor; // Motor for opening/closing the claw

    private static final double CLAW_OPEN_POWER = 0.5; // Reduced power for opening
    private static final double CLAW_CLOSED_POWER = 0; // Reduced power for closing

    private boolean clawOpen = true; // Track claw state

    @Override
    public void init() {
        armServo = hardwareMap.get(CRServo.class, "armServo");
        clawMotor = hardwareMap.get(Servo.class, "clawMotor");

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        // Arm rotation using the joystick (left stick x-axis)
        double armPower = gamepad1.left_stick_x * 0.5; // Scale down power to prevent excessive speed
        armServo.setPower(armPower);

        // Open/Close claw functionality using buttons
        if (gamepad1.a) {
            // Open the claw
            clawMotor.setPosition(CLAW_OPEN_POWER);
            clawOpen = true;
        } else if (gamepad1.b) {
            // Close the claw
            clawMotor.setPosition(CLAW_CLOSED_POWER);
            clawOpen = false;
        }

        // Telemetry for debugging
        telemetry.addData("Arm Servo Power", armPower);
        telemetry.addData("Claw State", clawOpen ? "Open" : "Closed");
        telemetry.update();
    }
}
