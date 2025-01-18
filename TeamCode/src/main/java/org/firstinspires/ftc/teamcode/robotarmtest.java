package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Dual Axon Servo Control")
public class robotarmtest extends LinearOpMode {
    private Servo axon1;
    private Servo axon2;

    // Constants for servo control
    private static final double STEP_SIZE = 0.005; // Joystick step size
    private static final double MIN_POSITION = 0.0; // Servo minimum position
    private static final double MAX_POSITION = 2.0; // Increased max position (you can change this if needed)

    // Initial servo positions (flipping the initial positions)
    private double axon1Position = 0.0; // Starting position for axon1 (0.0)
    private double axon2Position = 1.0; // Starting position for axon2 (1.0)

    @Override
    public void runOpMode() {
        // Initialize hardware
        axon1 = hardwareMap.get(Servo.class, "axon1");
        axon2 = hardwareMap.get(Servo.class, "axon2");

        // Configure servo range
        axon1.scaleRange(MIN_POSITION, MAX_POSITION);
        axon2.scaleRange(MIN_POSITION, MAX_POSITION);

        // Set initial positions
        axon1.setPosition(axon1Position);
        axon2.setPosition(axon2Position);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Initial Positions", "axon1: %.2f, axon2: %.2f", axon1Position, axon2Position);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Read joystick input
            double joystickInput = -gamepad1.right_stick_y;

            if (Math.abs(joystickInput) > 0.1) { // Deadzone to avoid accidental movement
                // Adjust positions based on joystick input
                axon1Position += joystickInput * STEP_SIZE;
                axon2Position -= joystickInput * STEP_SIZE;

                // Clamp positions within valid range
                axon1Position = Math.max(MIN_POSITION, Math.min(MAX_POSITION, axon1Position));
                axon2Position = Math.max(MIN_POSITION, Math.min(MAX_POSITION, axon2Position));

                // Update servo positions
                axon1.setPosition(axon1Position);
                axon2.setPosition(axon2Position);

                telemetry.addData("Joystick Input", joystickInput);
            }

            telemetry.addData("Servo Positions", "axon1: %.2f, axon2: %.2f", axon1Position, axon2Position);
            telemetry.update();
        }
    }
}
