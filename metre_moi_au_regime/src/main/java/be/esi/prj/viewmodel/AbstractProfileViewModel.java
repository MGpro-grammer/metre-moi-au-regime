package be.esi.prj.viewmodel;

import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;
import be.esi.prj.model.UserFacade;
import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * This is the base class for all profile view models.
 * It contains common properties and methods used by user profile screens.
 * Other view models can extend this class to share the same basic functionality.
 */
public abstract class AbstractProfileViewModel {
    protected final UserFacade userFacade;

    private final StringProperty name;
    private final IntegerProperty age;
    private final ObjectProperty<Gender> gender;
    private final DoubleProperty height;
    private final DoubleProperty weight;
    private final DoubleProperty goalWeight;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final ObjectProperty<ActivityLevel> activityLevel;

    private final StringProperty statusMessage;

    /**
     * Creates a new AbstractProfileViewModel with a user facade.
     * Sets up all the properties that store user profile information.
     * @param userFacade the service to handle user operations
     */
    protected AbstractProfileViewModel(UserFacade userFacade) {
        this.userFacade = userFacade;

        this.name = new SimpleStringProperty();
        this.age = new SimpleIntegerProperty();
        this.gender = new SimpleObjectProperty<>();
        this.height = new SimpleDoubleProperty();
        this.weight = new SimpleDoubleProperty();
        this.goalWeight = new SimpleDoubleProperty();
        this.startDate = new SimpleObjectProperty<>();
        this.endDate = new SimpleObjectProperty<>();
        this.activityLevel = new SimpleObjectProperty<>();

        this.statusMessage = new SimpleStringProperty();
    }

    /**
     * Saves the profile information.
     * This method must be implemented by classes that extend this one.
     * @return true if the save was successful, false if there was a problem
     */
    public abstract boolean save();

    /**
     * Checks if all the profile information is correct and complete.
     * Validates name, age, weight, height, dates, and other required fields.
     * Shows error messages if something is wrong.
     * @return true if all information is valid, false if there are errors
     */
    protected boolean areProfileDetailsValid() {
        if (nameProperty().get() == null || nameProperty().get().isBlank()) {
            statusMessage.set("Name cannot be empty.");
            return false;
        }
        if (ageProperty().get() <= 0) {
            statusMessage.set("Age must be a positive number.");
            return false;
        }
        if (ageProperty().get() > 120) {
            statusMessage.set("Age must be realistic.");
            return false;
        }
        if (weightProperty().get() <= 0 || heightProperty().get() <= 0) {
            statusMessage.set("Weight and height must be positive numbers.");
            return false;
        }
        if (weightProperty().get() > 1000 || heightProperty().get() > 300) {
            statusMessage.set("Weight and height must be realistic values.");
            return false;
        }
        if (startDateProperty().get() == null || endDateProperty().get() == null ||
                !startDateProperty().get().isBefore(endDateProperty().get())) {
            statusMessage.set("Start date must be before end date.");
            return false;
        }
        if (genderProperty().get() == null) {
            statusMessage.set("Gender must be selected.");
            return false;
        }
        if (goalWeightProperty().get() <= 0) {
            statusMessage.set("Goal weight must be a positive number.");
            return false;
        }
        if (activityLevelProperty().get() == null) {
            statusMessage.set("Activity level must be selected.");
            return false;
        }
        return true;
    }

    /**
     * Sets the user's name.
     * @param name the user's full name
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Sets the user's age.
     * @param age the user's age in years
     */
    public void setAge(int age) {
        this.age.set(age);
    }

    /**
     * Sets the user's gender.
     * @param gender the user's gender
     */
    public void setGender(Gender gender) {
        this.gender.set(gender);
    }

    /**
     * Sets the user's height.
     * @param height the user's height in centimeters
     */
    public void setHeight(double height) {
        this.height.set(height);
    }

    /**
     * Sets the user's current weight.
     * @param weight the user's weight in kilograms
     */
    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    /**
     * Sets the user's target weight.
     * @param goalWeight the weight the user wants to reach in kilograms
     */
    public void setGoalWeight(double goalWeight) {
        this.goalWeight.set(goalWeight);
    }

    /**
     * Sets when the user starts their goal.
     * @param startDate the date when the user begins their weight goal
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    /**
     * Sets when the user wants to reach their goal.
     * @param endDate the date when the user wants to achieve their target weight
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    /**
     * Sets how active the user is.
     * @param activityLevel the user's activity level
     */
    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel.set(activityLevel);
    }

    /**
     * Sets a status message to show to the user.
     * @param message the message to display (like error or success messages)
     */
    public void setStatusMessage(String message) {
        this.statusMessage.set(message);
    }

    /**
     * Gets the property that holds the user's name.
     * Used by JavaFX to bind to user interface elements.
     * @return the name property
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Gets the property that holds the user's age.
     * Used by JavaFX to bind to user interface elements.
     * @return the age property
     */
    public IntegerProperty ageProperty() {
        return age;
    }

    /**
     * Gets the property that holds the user's gender.
     * Used by JavaFX to bind to user interface elements.
     * @return the gender property
     */
    public ObjectProperty<Gender> genderProperty() {
        return gender;
    }

    /**
     * Gets the property that holds the user's height.
     * Used by JavaFX to bind to user interface elements.
     * @return the height property
     */
    public DoubleProperty heightProperty() {
        return height;
    }

    /**
     * Gets the property that holds the user's weight.
     * Used by JavaFX to bind to user interface elements.
     * @return the weight property
     */
    public DoubleProperty weightProperty() {
        return weight;
    }

    /**
     * Gets the property that holds the user's target weight.
     * Used by JavaFX to bind to user interface elements.
     * @return the goal weight property
     */
    public DoubleProperty goalWeightProperty() {
        return goalWeight;
    }

    /**
     * Gets the property that holds the user's start date.
     * Used by JavaFX to bind to user interface elements.
     * @return the start date property
     */
    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    /**
     * Gets the property that holds the user's end date.
     * Used by JavaFX to bind to user interface elements.
     * @return the end date property
     */
    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    /**
     * Gets the property that holds the user's activity level.
     * Used by JavaFX to bind to user interface elements.
     * @return the activity level property
     */
    public ObjectProperty<ActivityLevel> activityLevelProperty() {
        return activityLevel;
    }

    /**
     * Gets the user's name.
     * @return the user's full name
     */
    public String getName() {
        return name.get();
    }

    /**
     * Gets the user's age.
     * @return the user's age in years
     */
    public int getAge() {
        return age.get();
    }

    /**
     * Gets the user's gender.
     * @return the user's gender
     */
    public Gender getGender() {
        return gender.get();
    }

    /**
     * Gets the user's height.
     * @return the user's height in centimeters
     */
    public double getHeight() {
        return height.get();
    }

    /**
     * Gets the user's current weight.
     * @return the user's weight in kilograms
     */
    public double getWeight() {
        return weight.get();
    }

    /**
     * Gets the user's target weight.
     * @return the weight the user wants to reach in kilograms
     */
    public double getGoalWeight() {
        return goalWeight.get();
    }

    /**
     * Gets when the user starts their goal.
     * @return the date when the user begins their weight goal
     */
    public LocalDate getStartDate() {
        return startDate.get();
    }

    /**
     * Gets when the user wants to reach their goal.
     * @return the date when the user wants to achieve their target weight
     */
    public LocalDate getEndDate() {
        return endDate.get();
    }

    /**
     * Gets how active the user is.
     * @return the user's activity level
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel.get();
    }

    /**
     * Gets the current status message.
     * @return the message being displayed to the user
     */
    public String getStatusMessage() {
        return statusMessage.get();
    }
}
