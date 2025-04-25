# keycloak-required-action-user-validated
A Keycloak extension which provides a new Required Action named "User Must Be Validated", which does not allow the users login if not previously validated by an admin.
The admin has to assign a special role to the user account (like "validated_user") to validate it. 
While the user is not validated an error message will be shown like "The account is pendig for validation".
Another special role can be assigned to the users (like "rejected_user") and then another error message will be shown like "This user account has not been accepted".

Both roles and both messages are customizable, see the section ["Configuration parameters"](#configuration-parameters) below.

## Why use it
The registration (or access with external IDP) is free, there is no control by default in Keycloak, 
but a new user obviously should not be able to use the services until validated and granted permissions (assigned some role, wheter "realm role" or "client role").
As that new user doesn't have permissions, the web pages of different services/applications will appear empty 
or will show an error message like "missing permissions" or similar, which can be confusing to the user.
So that extensions provides a way to add an access control in the login flows (a Required Action) 
to properly inform the user that the account is pending for validation.

### Build
Use maven to generate the JAR file:
```
mvn package
```

### Install
First you must create the two special roles in the menu "Realm roles". 
By default they are "validated_user" and "rejected_user", but you can change these names, see the section ["Configuration parameters"](#configuration-parameters) below.

Then simply add the JAR file to the directory "providers" within keycloak working directory (if you use the official container image, it is `/opt/keycloak/providers`).  
And configure if you want, see the section ["Configuration parameters"](#configuration-parameters) below.

After restart the server you will see the new Required Action "User Must Be Validated" in the menu "Authentication", tab "Required actions".
Then you shoud "Enable" it and "Set as default action", so it will be automatically added to new registered users.

### Compatible versions
It has been tested with Keycloak 26.2.0.

### Configuration parameters
 - `roleForValidated`: default value "validated_user"
 - `roleForRejected`: default value "rejected_user"
 - `messageForPending`: default value "The account is pendig for validation."
 - `messageForRejected`: default value "This user account has not been accepted."
 - `automaticallyAddRequiredActionIfNotValidatedUser`: default value "no", possible values "yes", "no", "true", "false"
 
All these parameteres can be added: 
 - to the command line, just adding the prefix "--spi-required-action-user-validated-" and replacing camel-case with dashes "-".  
   (example: `start --spi-required-action-user-validated-role-for-validated=validated_user`)
 - or to the environment variables, just adding the prefix "KC_SPI_REQUIRED_ACTION_USER_VALIDATED_" and replacing camel-case with underline "_".  
   (example: `export KC_SPI_REQUIRED_ACTION_USER_VALIDATED_ROLE_FOR_VALIDATED=validated_user`)

The final assigned values are shown in the Keycloak log and in the Server Info page.


### References/documentation
Official keycloak documentation about extension development using SPI framwork:  
https://www.keycloak.org/docs/latest/server_development/index.html#_extensions

Other useful links:
https://github.com/keycloak/keycloak/blob/archive/release/25.0/services/src/main/java/org/keycloak/authentication/requiredactions/TermsAndConditions.java
https://www.keycloak.org/docs-api/latest/javadocs/org/keycloak/forms/login/LoginFormsProvider.html#createResponse(org.keycloak.models.UserModel.RequiredAction)
https://github.com/dasniko/keycloak-extensions-demo/blob/main/requiredaction/src/main/java/dasniko/keycloak/requiredaction/PhoneNumberRequiredAction.java
