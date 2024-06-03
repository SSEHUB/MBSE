package demo;

import properties.ProgramSettings;
import properties.Speed;

/**
 * Example program settings instance. Using constants is just one form of implementing this required class.
 * 
 * @author SSE
 */
public class MyProgramSettings extends ProgramSettings {

    @Override
    public int getFloorsHeight() {
        return 50;
    }

    @Override
    public boolean isAutoscroll() {
        return false;
    }

    
    
    @Override
    public int getFloors() {
        return 5;
    }
    
    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    public int getElevators() {
        return 1;
    }

    @Override
    public int getElevatorsSpeed() {
        return Speed.MEDIUM.getValue();
    }

    @Override
    public int getFloorsButtons() {
        return 1;
    }

    @Override
    public boolean isAccelerated() {
        return false;
    }

    @Override
    public boolean isOuterviewCancel() {
        return false;
    }

    @Override
    public boolean isInnerviewEmergency() {
        return true;
    }

    @Override
    public boolean isOuterviewEmergency() {
        return false;
    }

    @Override
    public boolean isDisplayDirection() {
        return true;
    }

    @Override
    public boolean isDisplayFloorNumber() {
        return true;
    }

    @Override
    public boolean isDisplayTarget() {
        return true;
    }

    @Override
    public boolean isInnerviewAuthorization() {
        return false;
    }

    @Override
    public boolean isDisplayDoorstate() {
        return false;
    }

    @Override
    public boolean isFloorSliderDisplay() {
        return false;
    }

    @Override
    public boolean isInnerviewDoorButton() {
        return false;
    }

}
