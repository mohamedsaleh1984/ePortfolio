### Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address?
The requirements are as follow
1. Login screen for the user to log in to the app
2. User registration functions
3. Present the items and their quantities; allow the user to add new items, delete items, and modify the details
4. Allow the user to send SMS messages

### What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?
I developed multiple screens to fulfill the app requirements. The main screen/activity was the inventory screen, where I presented all items and their information. The initial design contained two buttons at the top to send messages and add new items and a grid to present the items. I replaced those with two Floating Action Buttons to better utilize the full screen for the grid. I followed design guidelines in terms of alignments, fonts, spacing between UI elements, and screen boundaries.

### How did you approach the process of coding your app? What techniques or strategies did you use? How could those techniques or strategies be applied in the future?
After I laid down all the intended designs for my application, I started working on the core helper functionality. I began by defining the business classes or data transfer objects. I developed multiple interfaces to add abstraction to my classes, which can help later if I decide to switch the core implementation to something different.

For example, I used a MySQL database implementation by utilizing the interfaces. If I want to migrate to Firebase or another database technology, I can simply implement the same interfaces without breaking the code across the app. I also developed helper classes for common functions.

These features are already widely applied in the industry using dependency injection and other software engineering principles.


### How did you test to ensure your code was functional? Why is this process important, and what did it reveal?
I applied manual testing, where I go through each activity and test if the app behaves as expected. The process is important to identify defects in the app and determine where it lags or behaves abnormally. It's also important as a sanity check.

At times, we assume the app will function normally without issues when applying certain functions, but we might miss an important parameter that causes the function to behave completely differently. We overload a lot of SDK functionality, and that creates major bugs.

The testing process is a crucial and important step to validate the application's output and behavior.
### Consider the full app design and development process from initial planning to finalization. 

### Where did you have to innovate to overcome a challenge?
It depends on the project and environmental constraints. Working with different technologies to achieve one goal with no compatibility can be very challenging.

I had a case where I needed to integrate two technologies. The first was hardware connected to an Arduino, and the other was a Micromedia Flash AS3 application, which doesn't have any interfaces to communicate with serial ports or the Arduino kit. So, I created a bridge between both using C#. It was a successful solution that allowed the project to be completed.

### In what specific component of your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?
In the inventory screen, I used different UI elements to interact with the items and utilized Floating Action Buttons as well. The transition from the "Add New Item" screen to the inventory activity was implemented smoothly. Allowing the user to store a product image and display it as a UI element was a bit challenging, but I wanted to include that feature.
Also, working on the adapter was crucial for binding the DTO (Data Transfer Object) to the front end using a GridItemView.
