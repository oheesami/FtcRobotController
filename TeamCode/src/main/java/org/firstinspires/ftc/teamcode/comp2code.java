package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import java.util.concurrent.TimeUnit;


@TeleOp(name = "Basic Drive")
public class comp2code extends OpMode {
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor rightPulleyMotor;
    private DcMotor leftPulleyMotor;
    private CRServo axonServo;
    private Servo clawServo;
    private boolean clawClosed;
    private ServoController ControlHub_ServoController;
    private Servo axon1;
    private Servo axon2;

    // Constants for servo control
    private static final double STEP_SIZE = 0.01; // Joystick step size
    private static final double MIN_POSITION = 0.0; // Servo minimum position
    private static final double MAX_POSITION = 1.0; // Servo maximum position

    // Initial servo positions (flipping the initial positions)
    private double axon1Position = 1.0; // Starting position for axon1 (was 0.0)
    private double axon2Position = 0.0; // Starting position for axon2 (was 1.0)


    @Override
    public void init() {
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRight");
        rightPulleyMotor = hardwareMap.get(DcMotor.class, "rightPulley");
        leftPulleyMotor = hardwareMap.get(DcMotor.class, "leftPulley");
        axon1 = hardwareMap.get(Servo.class, "axon1");
        axon2 = hardwareMap.get(Servo.class, "axon2");
        clawClosed = false;
        ControlHub_ServoController = hardwareMap.get(ServoController.class, "Control Hub");
        ControlHub_ServoController.pwmEnable();

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightPulleyMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftPulleyMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Configure servo range
        axon1.scaleRange(MIN_POSITION, MAX_POSITION);
        axon2.scaleRange(MIN_POSITION, MAX_POSITION);

        // Set initial positions (flipped)
        axon1.setPosition(axon1Position);
        axon2.setPosition(axon2Position);

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
        double rightPulleyPower = gamepad2.dpad_up ? 1.0 : 0.0;
        double leftPulleyPower = gamepad2.dpad_up ? 1.0 : 0.0;
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
            if (clawClosed) {
                clawServo.setPosition(0.35);
                clawClosed = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                clawServo.setPosition(0);
                clawClosed = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (gamepad2.left_stick_y > 0) {
            axonServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (gamepad2.left_stick_y < 0) {
            axonServo.setDirection(DcMotorSimple.Direction.REVERSE);
        }

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

        frontLeftMotor.setPower(leftFrontPower * 0.7);
        frontRightMotor.setPower(rightFrontPower * 0.7);
        backLeftMotor.setPower(leftBackPower * 0.7);
        backRightMotor.setPower(rightBackPower * 0.7);
        rightPulleyMotor.setPower(rightPulleyPower * 0.7);
        leftPulleyMotor.setPower(leftPulleyPower * 0.7);
        axonServo.setPower(Math.abs(gamepad2.left_stick_y));

        // Show wheel power for debugging
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("Claw Servo Position", clawServo.getPosition());
        telemetry.update();
    }
}
