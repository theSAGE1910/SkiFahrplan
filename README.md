# Ski Route Planner (SkiFahrplan) ⛷️🏔️

The **Ski Route Planner** is an interactive command-line application designed to help skiers navigate a ski resort. By loading a map of lifts and pistes, the application can generate customized ski routes based on the user's skill level, goals, and personal preferences for certain slope conditions. 

## 🚀 Features

* **Interactive Command-Line Interface**: Processes a variety of user commands to manage the skiing session.
* **Graph-Based Ski Area**: Represents the ski resort as a network of interconnected lifts (e.g., GONDOLA, CHAIRLIFT) and pistes (e.g., BLUE, RED, BLACK).
* **Personalized Routing**: Routes are calculated taking into account the skier's skill level (e.g., BEGINNER, EXPERT) and primary goal (e.g., DISTANCE, ALTITUDE).
* **Preference Management**: Users can explicitly `like` or `dislike` specific piste features (like ICY or BUMPY conditions) to tailor their route.
* **Dynamic Navigation**: Step-by-step traversal of the planned route with options to take alternative paths dynamically.

## 🏗️ Architecture

The application initializes a `SkiArea` and a `Skier` profile, which are then managed via a `SkiSession`. 
User input is captured by the `CommandProcessor` which parses commands and delegates execution using the Command Design Pattern to specialized classes like `PlanCommand`, `TakeCommand`, and `LoadCommand`.

## ⌨️ Available Commands

Here are the commands supported by the `CommandProcessor`:

| Command | Description |
| :--- | :--- |
| `load area <path>` | Loads the ski area configuration from a text file. |
| `list` | Lists properties of the ski area (e.g., lifts, pistes). |
| `set skill <level>` | Sets the skier's skill level (e.g., `BEGINNER`, `EXPERT`). |
| `set goal <goal>` | Sets the target goal for the session (e.g., `DISTANCE`, `ALTITUDE`). |
| `like <feature>` | Adds a positive preference for a piste feature (e.g., `BLUE`, `ICY`). |
| `dislike <feature>`| Adds a negative preference to avoid a piste feature (e.g., `BUMPY`). |
| `reset` | Resets the current preferences. |
| `plan <start> <time1> <time2>` | Calculates a route starting from a specific lift/node within a time window. |
| `show route` | Displays the sequentially planned route of lifts and pistes. |
| `next` | Peeks at the next step in the planned route. |
| `take` | Confirms and takes the next step in the route. |
| `alternative` | Requests an alternative route to avoid the current upcoming node. |
| `abort` | Aborts the current planned route. |
| `quit` | Exits the application. |

## 📖 Example Usage

```text
> load area ./input/area.txt
> set skill BEGINNER
> set goal DISTANCE
> like BLUE
> dislike BUMPY
> plan LiftA 11:00 12:30
route planned
> show route
LiftA Piste1 LiftB Piste3 LiftA Piste1 LiftB Piste3
> next
LiftA
> take
> quit
