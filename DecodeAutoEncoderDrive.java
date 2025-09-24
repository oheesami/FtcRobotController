// File: DecodeAutoEncoderDrive.java
package org.firstinspires.ftc.teamcode.auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Decode Challenge: Encoder Auto Drive", group = "Decode")
public class DecodeAutoEncoderDrive extends LinearOpMode {

    // Hardware mapping - edit these names to match your robot config
    private DcMotorEx leftFront = null;
    private DcMotorEx rightFront = null;
    private DcMotorEx leftBack = null;
    private DcMotorEx rightBack = null;

    // DRIVE CONSTANTS - adjust to your specific robot
    private static final double TICKS_PER_REV = 560.0;   // Change to your encoder (e.g., 560 for REV/NEVEREST)
    private static final double GEAR_RATIO = 1.0;        // Change if gearbox present
    private static final double WHEEL_DIAMETER_IN = 4.0; // Change to your wheel size
    private static final double TRACK_WIDTH_IN = 14.0;   // Distance between left and right wheel centers

    // Calculated conversion factor
    private static final double COUNTS_PER_INCH = (TICKS_PER_REV * GEAR_RATIO) / (Math.PI * WHEEL_DIAMETER_IN);

    // Movement parameters
    private static final double DRIVE_POWER = 0.6;
    private static final double TURN_POWER = 0.5;
    private static final double STRAFE_POWER = 0.7;
    
    // Precision movement parameters
    private static final double SLOW_POWER = 0.3;
    private static final double PRECISION_THRESHOLD = 2.0; // inches for precision approach

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        initializeHardware();
        
        telemetry.addData("Status", "Initialized - Ready for Decode Challenge");
        telemetry.addData("Drive System", "Mecanum with Encoders");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        runtime.reset();
        
        // DECODE CHALLENGE AUTONOMOUS ROUTINE
        // Customize this sequence based on your specific Decode challenge strategy
        executeDecodeSequence();

        // Final status
        stopAllMotors();
        telemetry.addData("Status", "Decode Challenge Complete");
        telemetry.addData("Runtime", "%.2f seconds", runtime.seconds());
        telemetry.update();
    }

    /**
     * Initialize all hardware components
     */
    private void initializeHardware() {
        // Hardware mapping
        leftFront = hardwareMap.get(DcMotorEx.class, "left_front");
        rightFront = hardwareMap.get(DcMotorEx.class, "right_front");
        leftBack = hardwareMap.get(DcMotorEx.class, "left_back");
        rightBack = hardwareMap.get(DcMotorEx.class, "right_back");

        // Set motor directions (adjust based on your robot's wiring)
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        // Reset encoders and set behavior
        resetAndConfigureEncoders();
    }

    /**
     * Execute the main autonomous sequence for Decode challenge
     * Customize this method based on your team's strategy
     */
    private void executeDecodeSequence() {
        telemetry.addData("Phase", "Starting Decode Sequence");
        telemetry.update();

        // Example sequence - modify based on your Decode challenge strategy
        // Phase 1: Navigate to first position
        encoderDrive(DRIVE_POWER, 30, 5.0, "Moving to first position");
        
        // Phase 2: Strafe to align with target
        encoderStrafe(STRAFE_POWER, 18, 4.0, "Aligning with target");
        
        // Phase 3: Precision approach
        encoderDrive(SLOW_POWER, 6, 3.0, "Precision approach");
        
        // Phase 4: Turn to face next objective
        encoderTurn(TURN_POWER, 90, 4.0, "Turning to next objective");
        
        // Phase 5: Navigate to second position
        encoderDrive(DRIVE_POWER, 24, 4.0, "Moving to second position");
        
        // Add more phases as needed for your specific Decode challenge strategy
    }

    /**
     * Reset encoders and configure motor behavior
     */
    private void resetAndConfigureEncoders() {
        DcMotorEx[] motors = {leftFront, rightFront, leftBack, rightBack};
        
        for (DcMotorEx motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        
        setDrivePower(0, 0, 0, 0);
    }

    /**
     * Set power to all drive motors
     */
    private void setDrivePower(double lf, double rf, double lb, double rb) {
        leftFront.setPower(lf);
        rightFront.setPower(rf);
        leftBack.setPower(lb);
        rightBack.setPower(rb);
    }

    /**
     * Stop all motors immediately
     */
    private void stopAllMotors() {
        setDrivePower(0, 0, 0, 0);
        
        // Reset to encoder mode for potential manual control
        DcMotorEx[] motors = {leftFront, rightFront, leftBack, rightBack};
        for (DcMotorEx motor : motors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /**
     * Drive straight for specified distance with telemetry feedback
     * @param speed Power level (0.0 to 1.0)
     * @param inches Distance to travel (positive = forward, negative = backward)
     * @param timeoutS Maximum time to allow for movement
     * @param description Description for telemetry
     */
    private void encoderDrive(double speed, double inches, double timeoutS, String description) {
        if (!opModeIsActive()) return;

        int moveCounts = (int) Math.round(inches * COUNTS_PER_INCH);
        
        // Calculate new target positions
        int newLF = leftFront.getCurrentPosition() + moveCounts;
        int newRF = rightFront.getCurrentPosition() + moveCounts;
        int newLB = leftBack.getCurrentPosition() + moveCounts;
        int newRB = rightBack.getCurrentPosition() + moveCounts;

        // Set target positions
        leftFront.setTargetPosition(newLF);
        rightFront.setTargetPosition(newRF);
        leftBack.setTargetPosition(newLB);
        rightBack.setTargetPosition(newRB);

        // Switch to RUN_TO_POSITION mode
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Start movement
        setDrivePower(Math.abs(speed), Math.abs(speed), Math.abs(speed), Math.abs(speed));

        // Wait for completion with enhanced telemetry
        waitForMovementCompletion(timeoutS, description);

        // Stop and return to encoder mode
        stopAllMotors();
    }

    /**
     * Strafe left/right for specified distance
     * @param speed Power level (0.0 to 1.0)
     * @param inches Distance to strafe (positive = right, negative = left)
     * @param timeoutS Maximum time to allow for movement
     * @param description Description for telemetry
     */
    private void encoderStrafe(double speed, double inches, double timeoutS, String description) {
        if (!opModeIsActive()) return;

        int moveCounts = (int) Math.round(inches * COUNTS_PER_INCH);
        
        // Mecanum strafe pattern: LF+, RF-, LB-, RB+
        int newLF = leftFront.getCurrentPosition() + moveCounts;
        int newRF = rightFront.getCurrentPosition() - moveCounts;
        int newLB = leftBack.getCurrentPosition() - moveCounts;
        int newRB = rightBack.getCurrentPosition() + moveCounts;

        leftFront.setTargetPosition(newLF);
        rightFront.setTargetPosition(newRF);
        leftBack.setTargetPosition(newLB);
        rightBack.setTargetPosition(newRB);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set strafe power pattern
        setDrivePower(Math.abs(speed), -Math.abs(speed), -Math.abs(speed), Math.abs(speed));

        waitForMovementCompletion(timeoutS, description);
        stopAllMotors();
    }

    /**
     * Turn in place by specified degrees
     * @param speed Power level (0.0 to 1.0)
     * @param degrees Degrees to turn (positive = right, negative = left)
     * @param timeoutS Maximum time to allow for movement
     * @param description Description for telemetry
     */
    private void encoderTurn(double speed, double degrees, double timeoutS, String description) {
        if (!opModeIsActive()) return;

        // Calculate arc length for turn
        double turnCircumference = Math.PI * TRACK_WIDTH_IN;
        double inchesForDegrees = (turnCircumference * (degrees / 360.0));
        int moveCounts = (int) Math.round(inchesForDegrees * COUNTS_PER_INCH);

        // For right turn: left wheels forward, right wheels backward
        int newLF = leftFront.getCurrentPosition() + moveCounts;
        int newLB = leftBack.getCurrentPosition() + moveCounts;
        int newRF = rightFront.getCurrentPosition() - moveCounts;
        int newRB = rightBack.getCurrentPosition() - moveCounts;

        leftFront.setTargetPosition(newLF);
        leftBack.setTargetPosition(newLB);
        rightFront.setTargetPosition(newRF);
        rightBack.setTargetPosition(newRB);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set turn power pattern
        setDrivePower(Math.abs(speed), -Math.abs(speed), Math.abs(speed), -Math.abs(speed));

        waitForMovementCompletion(timeoutS, description);
        stopAllMotors();
    }

    /**
     * Set all motors to the same mode
     */
    private void setMotorMode(DcMotor.RunMode mode) {
        leftFront.setMode(mode);
        rightFront.setMode(mode);
        leftBack.setMode(mode);
        rightBack.setMode(mode);
    }

    /**
     * Wait for movement completion with enhanced telemetry and timeout handling
     */
    private void waitForMovementCompletion(double timeoutS, String description) {
        ElapsedTime moveTimer = new ElapsedTime();
        moveTimer.reset();

        while (opModeIsActive() && 
               moveTimer.seconds() < timeoutS && 
               (leftFront.isBusy() || rightFront.isBusy() || leftBack.isBusy() || rightBack.isBusy())) {
            
            // Enhanced telemetry for debugging and monitoring
            telemetry.addData("Action", description);
            telemetry.addData("Runtime", "%.1f/%.1f sec", moveTimer.seconds(), timeoutS);
            telemetry.addLine();
            telemetry.addData("LF Pos", "%d (target: %d)", 
                leftFront.getCurrentPosition(), leftFront.getTargetPosition());
            telemetry.addData("RF Pos", "%d (target: %d)", 
                rightFront.getCurrentPosition(), rightFront.getTargetPosition());
            telemetry.addData("LB Pos", "%d (target: %d)", 
                leftBack.getCurrentPosition(), leftBack.getTargetPosition());
            telemetry.addData("RB Pos", "%d (target: %d)", 
                rightBack.getCurrentPosition(), rightBack.getTargetPosition());
            telemetry.addLine();
            telemetry.addData("Motors Busy", "LF:%b RF:%b LB:%b RB:%b",
                leftFront.isBusy(), rightFront.isBusy(), leftBack.isBusy(), rightBack.isBusy());
            telemetry.update();

            // Small delay to prevent overwhelming the system
            sleep(50);
        }

        // Log completion status
        if (moveTimer.seconds() >= timeoutS) {
            telemetry.addData("Warning", "Movement timed out: " + description);
        } else {
            telemetry.addData("Complete", description);
        }
        telemetry.update();
    }
}
