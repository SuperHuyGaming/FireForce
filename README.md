##FireForce: Fire Simulation and Management System

##Table of Contents
    #Description
    #How it works
    #Setup and Usage
    #Contributing
    #License
    #Contact


##Description
#FireForce is a Java-based fire emergency simulation system that models the spread of fires and the response of fire stations. This project is designed to track active fires, prioritize emergency responses, and deploy fire trucks based on severity and distance. The simulation includes dynamic fire spreading, real-time station coordination, and automated fire suppression strategies.

##Features
    # Fire Simulation:Fires are generated with random coordinates, severity, and timestamps. Fires can spread to nearby locations over time, with a 30% chance of spreading every 20 seconds.New fires are slightly less severe than the original fire.
    # Fire Station Management: Fire stations are added to the simulation and can deploy fire trucks. Fire stations calculate their distance to active fires and prioritize deployment based on proximity.
    # Fire Truck Deployment: The number of fire trucks deployed is determined by the severity of the fire. Fire stations deploy trucks if they are available, and the system continues searching for available stations until the required number of trucks is deployed.
    # Automatic Fire Spread: A scheduled task runs every 20 seconds to simulate fire spreading and updates the state of active fires.
    # Fire Comparison: The system identifies the most severe fire based on severity and timestamp.

##How it works
    # GenSituationClass: Manages the simulation, including active fires, fire stations, and fire truck deployment. Handles fire spreading and automatic updates using a ScheduledExecutorService.
    # Fire: Represents Represents a fire with properties such as coordinates (x, y), severity, and timestamp. Includes methods to simulate fire spreading.
    # FireStation: Represents a fire station with properties such as ID, location, and available fire trucks. Includes methods to calculate distance to a fire and deploy fire trucks.

## Setup and Usage
    # Clone the repository: https://github.com/SuperHuyGaming/FireForce
    # Compile the Project: javac MiniFireForce/*.java
    # Run the Simulation: java MiniFireForce.GenSituationClass
    # Interact with the Simulation: The simulation will automatically generate fires and spread them over time, Fire stations will deploy trucks to extinguish fires based on their severity and proximity.


## Contributing
    1.=Fork the repository.
    2.=Create a new branch.
    3.=Make your changes.
    4.=Submit a pull request.

## License
#This project is licensed under the MIT License.

## Contact
    #[Khai Huynh] - [hquangkhai8@gmail.com]
    #[Phong Le] - [ltp161205@gmail.com]
    #[Huy Truong] - [minhhuyngoctruong@gmail.com]
    #[Khang Nguyen] - [nghkhang2005@gmail.com]##FireForce: Fire Simulation and Management System
