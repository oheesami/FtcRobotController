package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

@TeleOp(name = "Basic Drive")
public class DriveRobot extends OpMode {
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

    @Override
    public void init() {
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRight");
        rightPulleyMotor = hardwareMap.get(DcMotor.class, "rightPulley");
        leftPulleyMotor = hardwareMap.get(DcMotor.class, "leftPulley");
        axonServo = hardwareMap.get(CRServo.class, "axon");
        clawServo = hardwareMap.get(Servo.class, "armServo");
        ControlHub_ServoController = hardwareMap.get(ServoController.class, "Control Hub");
        ControlHub_ServoController.pwmEnable();
        clawClosed = false;

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightPulleyMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftPulleyMotor.setDirection(DcMotorSimple.Direction.REVERSE);

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
        double rightPulleyPower = gamepad1.dpad_up ? 1.0 : 0.0;
        double leftPulleyPower = gamepad1.dpad_up ? 1.0 : 0.0;
        rightPulleyPower = gamepad1.dpad_down ? -1.0 : rightPulleyPower;
        leftPulleyPower = gamepad1.dpad_down ? -1.0 : leftPulleyPower;

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

//        if (gamepad1.cross) {
//            if (clawClosed) {
//                clawServo.setPosition(1);
//                clawClosed = false;
//            } else {
//                clawServo.setPosition(0);
//                clawClosed = true;
//            }
//        }

        if (gamepad1.right_stick_y > 0) {
            axonServo.setDirection(DcMotorSimple.Direction.FORWARD);
        } else if (gamepad1.right_stick_y < 0) {
            axonServo.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        // When uncommented this code should make each motor spin forwards
        // when the respective button in pressed.
        //leftFrontPower  = gamepad1.cross ? 1.0 : 0.0;
        //leftBackPower   = gamepad1.circle ? 1.0 : 0.0;
        //rightFrontPower = gamepad1.square ? 1.0 : 0.0;
        //rightBackPower  = gamepad1.triangle ? 1.0 : 0.0;
        //rightPulleyPower = gamepad1.dpad_up ? 1.0 : 0.0;
        //leftPulleyPower = gamepad1.dpad_down ? 1.0 : 0.0;

        frontLeftMotor.setPower(leftFrontPower);
        frontRightMotor.setPower(rightFrontPower);
        backLeftMotor.setPower(leftBackPower);
        backRightMotor.setPower(rightBackPower);
        rightPulleyMotor.setPower(rightPulleyPower);
        leftPulleyMotor.setPower(leftPulleyPower);
        axonServo.setPower(Math.abs(gamepad1.right_stick_y));

        // Show wheel power for debugging
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.addData("Claw Servo Position", clawServo.getPosition());
        telemetry.update();
    }
}
