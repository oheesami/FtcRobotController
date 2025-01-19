package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import java.util.concurrent.TimeUnit;


@TeleOp(name = "Comp2Code")
public class comp2code extends OpMode {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor rightPulleyMotor;
    private DcMotor leftPulleyMotor;
    private ServoController ControlHub_ServoController;
    private Servo axon1;
    private Servo axon2;
    private Servo goBildaClaw;
    private Servo goBildaLift;
    private CRServo pushyLeft;
    private CRServo pushyRight;

    // Constants for servo positions
    private static final double CLAW_OPEN = 1.0;
    private static final double CLAW_CLOSE = 0.0;

    // Modified lift constants
    private static final double LIFT_MIN = 0;
    private static final double MOVE_SCALE = 0.005; // Even smaller for more precise control

    // Constants for servo control
    private static final double STEP_SIZE = 0.005; // Joystick step size
    private static final double MIN_POSITION = 0.0; // Servo minimum position
    private static final double MAX_POSITION = 1.0; // Servo maximum position

    // Initial servo positions (flipping the initial positions)
    private double axon1Position = 1.0; // Starting position for axon1 (was 0.0)
    private double axon2Position = 0.0; // Starting position for axon2 (was 1.0)

    double liftPosition = LIFT_MIN;


    @Override
    public void init() {
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRight");
        rightPulleyMotor = hardwareMap.get(DcMotor.class, "rightPulley");
        leftPulleyMotor = hardwareMap.get(DcMotor.class, "leftPulley");
        pushyLeft = hardwareMap.get(CRServo.class, "pushyLeft");
        pushyRight = hardwareMap.get(CRServo.class, "pushyRight");
        axon1 = hardwareMap.get(Servo.class, "axon1");
        axon2 = hardwareMap.get(Servo.class, "axon2");
        goBildaClaw = hardwareMap.get(Servo.class, "goBildaClaw");
        goBildaLift = hardwareMap.get(Servo.class, "goBildaLift");
        ControlHub_ServoController = hardwareMap.get(ServoController.class, "Control Hub");
        ControlHub_ServoController.pwmEnable();

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightPulleyMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftPulleyMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        goBildaLift.setDirection(Servo.Direction.FORWARD);

        // Configure servo range
        axon1.scaleRange(MIN_POSITION, MAX_POSITION);
        axon2.scaleRange(MIN_POSITION, MAX_POSITION);

        // Set initial positions (flipped)
        axon1.setPosition(axon1Position);
        axon2.setPosition(axon2Position);
        goBildaLift.setPosition(LIFT_MIN);
        goBildaClaw.setPosition(CLAW_CLOSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {}

    @Override
    public void start() {}

    @Override
    public void loop() {
        double max;

        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        double axial   = -gamepad1.left_stick_y;
        double lateral =  gamepad1.left_stick_x;
        double yaw     =  gamepad1.right_stick_x;

        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double leftFrontPower  = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower   = axial - lateral + yaw;
        double rightBackPower  = axial + lateral - yaw;
        double pushyLeftPower = Math.abs(gamepad1.left_trigger) > 0.1 ? 1.0 : 0.0;
        double pushyRightPower = Math.abs(gamepad1.left_trigger) > 0.1 ? -1.0 : 0.0;
        double rightPulleyPower = gamepad2.dpad_up ? 1.0 : 0.0;
        double leftPulleyPower = gamepad2.dpad_up ? 1.0 : 0.0;
        pushyLeftPower = Math.abs(gamepad1.right_trigger) > 0.1 ? -1.0 : pushyLeftPower;
        pushyRightPower = Math.abs(gamepad1.right_trigger) > 0.1 ? 1.0 : pushyRightPower;
        rightPulleyPower = gamepad2.dpad_down ? -1.0 : rightPulleyPower;
        leftPulleyPower = gamepad2.dpad_down ? -1.0 : leftPulleyPower;


        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower  /= max;
            rightFrontPower /= max;
            leftBackPower   /= max;
            rightBackPower  /= max;
        }

        if (gamepad2.a) {
            goBildaClaw.setPosition(CLAW_OPEN);
        } else if (gamepad2.b) {
            goBildaClaw.setPosition(CLAW_CLOSE);
        }

        // Manual position controls for testing
        if (gamepad1.left_bumper) {
            liftPosition += MOVE_SCALE;
            liftPosition = Math.min(0.5727, liftPosition); // Clamp within bounds
        }
        if (gamepad1.right_bumper) {
            liftPosition = Math.max(LIFT_MIN, liftPosition - MOVE_SCALE);
        }

        // Emergency reset with X button
        if (gamepad2.x) {
            liftPosition = LIFT_MIN;
        }

        if (Math.abs(gamepad2.right_stick_y) > 0.1) { // Deadzone to avoid accidental movement
            // Adjust positions based on joystick input
            if (-gamepad2.right_stick_y > 0) {
                axon1Position += STEP_SIZE;
                axon2Position -= STEP_SIZE;
            } else {
                axon1Position -= STEP_SIZE;
                axon2Position += STEP_SIZE;
            }

            if (axon1Position < 0.58) {
                axon1Position = 0.58;
            }
            if (axon2Position > 0.42) {
                axon2Position = 0.42;
            }

            // Clamp positions within valid range
            axon1Position = Math.max(MIN_POSITION, Math.min(MAX_POSITION, axon1Position));
            axon2Position = Math.max(MIN_POSITION, Math.min(MAX_POSITION, axon2Position));
        }

        axon1.setPosition(axon1Position);
        axon2.setPosition(axon2Position);
        goBildaLift.setPosition(liftPosition);
        frontLeftMotor.setPower(leftFrontPower * 0.7);
        frontRightMotor.setPower(rightFrontPower * 0.7);
        backLeftMotor.setPower(leftBackPower * 0.7);
        backRightMotor.setPower(rightBackPower * 0.7);
        rightPulleyMotor.setPower(rightPulleyPower * 0.7);
        leftPulleyMotor.setPower(leftPulleyPower * 0.7);
        pushyLeft.setPower(pushyLeftPower * 0.7);
        pushyRight.setPower(pushyRightPower * 0.7);

        // Show wheel power for debugging
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("Axon 1 position", axon1Position);
        telemetry.addData("Axon 2 position", axon2Position);
        telemetry.addData("Lift servo position", liftPosition);
        telemetry.update();
    }
}

