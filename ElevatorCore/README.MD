** Elevator Simulator **

Simple elevator simulator which can handle elevators in multiple wells with varying request handling strategies and optional synchronized multi-elevator control. Initially developed by Franziska Öllerer, Sebastian Schniedermeyer, Steven Bullert, Patrick Kolan, Robert Kapeluch in a lab course in winter semester 2009/10. Then used in EASy-Producer as example and test case for pre-processor based product line instantiation.

This is an extended fork of the EASy-Producer variant of the elevator simulator. Differences are
* Runtime configuration and instance factory instead of static configuration
* Elevator event handling (buttons, floor sensors, elevator sensors) for event-based strategies as opposed to original elevator call request strategies. Originally, elevator and panel buttons directly interacted with the request strategies. Now, buttons issue an internal (button) event and perform the UI actions, while a default event observer interacts with the default request strategies. The abstract event strategy overrides these observers and allows for lower event-based strategies, i.e., elevators moving until floor sensors indicate that the strategy shall stop the elevator as well as explicit door opening and closing.
* Cleaned up interfaces.
* English UI
* (Reverse-engineered) Documentation in English.

See the "test" code for an example how to "instantiate" and start this version.

Potential improvements
* UI design
* Floor names file, privileged floor and authorization
* Regression tests
* Improved documentation
* Door animation