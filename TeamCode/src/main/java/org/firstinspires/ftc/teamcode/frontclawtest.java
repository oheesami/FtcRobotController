package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "frontclawtest")
public class frontclawtest extends LinearOpMode {
    private Servo goBildaClaw;
    private Servo goBildaLift;

    // Constants for servo positions
    private static final double CLAW_OPEN = 1.0;
    private static final double CLAW_CLOSE = 0.0;

    // Modified lift constants
    private static final double LIFT_MIN = 0.3;
    private static final double MOVE_SCALE = 0.005; // Even smaller for more precise control

    @Override
    public void runOpMode() {
        goBildaClaw = hardwareMap.get(Servo.class, "goBildaClaw");
        goBildaLift = hardwareMap.get(Servo.class, "goBildaLift");

        // Set to forward direction explicitly
        goBildaLift.setDirection(Servo.Direction.FORWARD);

        // Set initial position
        goBildaLift.setPosition(LIFT_MIN);
        goBildaClaw.setPosition(CLAW_CLOSE);

        double currentPosition = LIFT_MIN;

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Claw control
            if (gamepad1.a) {
                goBildaClaw.setPosition(CLAW_OPEN);
            } else if (gamepad1.b) {
                goBildaClaw.setPosition(CLAW_CLOSE);
            }

            // Get joystick input
            double joystickInput = -gamepad1.left_stick_y;

            if (Math.abs(joystickInput) > 0.1) {
                // Allow moving up without limit, but keep minimum
                currentPosition += joystickInput * MOVE_SCALE;
                currentPosition = Math.max(LIFT_MIN, Math.min(1.0, currentPosition)); // Clamp within bounds

                goBildaLift.setPosition(currentPosition);
            }

            // Manual position controls for testing
            if (gamepad1.dpad_up) {
                currentPosition += MOVE_SCALE;
                currentPosition = Math.min(1.0, currentPosition); // Clamp within bounds
                goBildaLift.setPosition(currentPosition);
            }
            if (gamepad1.dpad_down) {
                currentPosition = Math.max(LIFT_MIN, currentPosition - MOVE_SCALE);
                goBildaLift.setPosition(currentPosition);
            }

            // Emergency reset with X button
            if (gamepad1.x) {
                currentPosition = LIFT_MIN;
                goBildaLift.setPosition(LIFT_MIN);
            }

            // Telemetry
            telemetry.addData("Target Position", String.format("%.3f", currentPosition));
            telemetry.addData("Actual Position", String.format("%.3f", goBildaLift.getPosition()));
            telemetry.addData("Joystick Value", String.format("%.3f", joystickInput));
            telemetry.update();
        }
    }
}
