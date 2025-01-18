package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Enhanced Drive")
public class driverobotishmaelversion extends OpMode {
    // Motor declarations for the drivetrain
    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    // Motors for the pulley system
    private DcMotor rightPulleyMotor, leftPulleyMotor;
    // Servos for claw mechanisms
    private Servo frontClawUpDownServo, frontClawOpenCloseServo;
    private Servo backClawUpDownServo, backClawOpenCloseServo;

    @Override
    public void init() {
        // Mapping drivetrain motors
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRight");

        // Mapping pulley system motors
        rightPulleyMotor = hardwareMap.get(DcMotor.class, "rightPulley");
        leftPulleyMotor = hardwareMap.get(DcMotor.class, "leftPulley");

        // Mapping servos for claws
        frontClawUpDownServo = hardwareMap.get(Servo.class, "frontClawUpDown");
        frontClawOpenCloseServo = hardwareMap.get(Servo.class, "frontClawOpenClose");
        backClawUpDownServo = hardwareMap.get(Servo.class, "backClawUpDown");
        backClawOpenCloseServo = hardwareMap.get(Servo.class, "backClawOpenClose");

        // Setting motor directions for proper movement
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Reading joystick values for omnidirectional movement
        double axial = -gamepad1.left_stick_y; // Forward/backward
        double lateral = gamepad1.left_stick_x; // Side-to-side
        double yaw = gamepad1.right_stick_x; // Rotation

        // Calculating power for each motor based on movement
        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        // Normalizing motor powers to stay within [-1, 1]
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Setting motor powers with a speed reduction factor
        frontLeftMotor.setPower(leftFrontPower * 0.7);
        frontRightMotor.setPower(rightFrontPower * 0.7);
        backLeftMotor.setPower(leftBackPower * 0.7);
        backRightMotor.setPower(rightBackPower * 0.7);

        // Controlling the pulley system using the D-pad
        double pulleyPower = gamepad2.dpad_up ? 1.0 : gamepad2.dpad_down ? -1.0 : 0.0;
        rightPulleyMotor.setPower(pulleyPower * 0.7);
        leftPulleyMotor.setPower(pulleyPower * 0.7);

        // Controlling the front claw's open/close functionality
        if (gamepad2.a) {
            frontClawOpenCloseServo.setPosition(1.0); // Open
        } else if (gamepad2.b) {
            frontClawOpenCloseServo.setPosition(0.0); // Close
        }

        // Controlling the front claw's up/down movement
        if (gamepad2.left_bumper) {
            frontClawUpDownServo.setPosition(1.0); // Up
        } else if (gamepad2.right_bumper) {
            frontClawUpDownServo.setPosition(0.0); // Down
        }

        // Controlling the back claw's open/close functionality
        if (gamepad2.x) {
            backClawOpenCloseServo.setPosition(1.0); // Open
        } else if (gamepad2.y) {
            backClawOpenCloseServo.setPosition(0.0); // Close
        }

        // Controlling the back claw's up/down movement
        if (gamepad2.dpad_left) {
            backClawUpDownServo.setPosition(1.0); // Up
        } else if (gamepad2.dpad_right) {
            backClawUpDownServo.setPosition(0.0); // Down
        }

        // Sending telemetry data for debugging and feedback
        telemetry.addData("Front Claw Position", frontClawOpenCloseServo.getPosition());
        telemetry.addData("Back Claw Position", backClawOpenCloseServo.getPosition());
        telemetry.update();
    }
}
