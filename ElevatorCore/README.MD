# Elevator Simulator

Simple elevator simulator which can handle elevators in multiple wells with varying request handling strategies and optional synchronized multi-elevator control. Initially developed by Franziska Öllerer, Sebastian Schniedermeyer, Steven Bullert, Patrick Kolan, Robert Kapeluch in a lab course in winter semester 2009/10. Then used in EASy-Producer as example and test case for pre-processor based product line instantiation.

This is an extended fork of the EASy-Producer variant of the elevator simulator. Differences are
* Runtime configuration and instance factory instead of static configuration
* Elevator event handling (buttons, floor sensors, elevator sensors) for event-based strategies as opposed to original elevator call request strategies. Originally, elevator and panel buttons directly interacted with the request strategies. Now, buttons issue an internal (button) event and perform the UI actions, while a default event observer interacts with the default request strategies. The abstract event strategy overrides these observers and allows for lower event-based strategies, i.e., elevators moving until floor sensors indicate that the strategy shall stop the elevator as well as explicit door opening and closing.
* Cleaned up interfaces.
* English UI
* (Reverse-engineered) Documentation in English.

## Getting Started
See the `demo` package in "test" code for an example how to use `ElevatorCore`. 

To configure the simulator, two components are required:
* `InstanceFactory` defines the behavior of the elevator setup by assigning controllers used in the simulation.

This project currently includes two implementations for single-elevator control (`DefaultEController` and `DistanceEController`) and one for multi-elevator control.  
Additionally, there is a third, event-based controller (`SimpleEventController`), which is available as an experimental implementation in the demo package.
* `ProgramSettings` defines the mostly static characteristics of the elevator setup (see details below).

**Building Configuration** (inside `ProgramSettings` class)
| Feature | Description  | Function Name |
|--|--| -- |
| Number of floors| Total number of floors in the building  | `getFloors` |
| Floor height | Height of each floor | `getFloorsHeight`  | 
| Number of buttons | Number of buttons on the panel outside the elevator |`getFloorsButtons` |
| Emergency button | Emergency control outside the elevator  | `isOuterviewEmergency` |
| Elevator call cancellation | Whether users can cancel an elevator call | `isOuterviewCancel`| 



**Elevator Configuration**  (inside `ProgramSettings` class)
| Feature | Description  | Function Name |
|--|--| -- |
| Number of elevators | Number of elevator units |`getElevators` |
| Speed | Elevator speed (`SLOW`, `MEDIUM` and `FAST`) |`getElevatorsSpeed` |
| Acceleration Mode | Whether the elevator uses acceleration mode | `isAccelerated`|
| Synchronized Mode |Whether the elevators operate in synchronized mode |`isSynchronized` | 
| Door button | Door control button inside the elevator (to open/close doors) |`isInnerviewDoorButton` |
| Authorization button | Button for user authorization inside the elevator | `isInnerviewAuthorization`| 
| Display direction | Shows the elevator's movement direction (inside elevator) | `isDisplayDirection`|
| Displays the target floor | Shows the target floor (inside elevator)| `isDisplayTarget`|
| Floor slider | Displays the floor slider (inside elevator)|`isFloorSliderDisplay` |
| Door state display  | Indicates whether the doors are open or closed (inside elevator) | `isDisplayDoorstate`|
| Current floor | Displays current floor number (inside elevator)| `isDisplayFloorNumber`|
| Emergency button| Emergency button (inside elevator) | `isInnerviewEmergency` | 


### Potential improvements
* UI design
* Floor names file, privileged floor and authorization
* Regression tests
* Improved documentation
* Door animation
