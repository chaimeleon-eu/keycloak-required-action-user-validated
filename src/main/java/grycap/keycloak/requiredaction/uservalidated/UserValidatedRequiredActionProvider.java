package grycap.keycloak.requiredaction.uservalidated;

import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.MessageType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;


public class UserValidatedRequiredActionProvider implements RequiredActionProvider {
    private static final Logger LOG = Logger.getLogger(UserValidatedRequiredActionProvider.class);

    public static String PROVIDER_ID = "USER_VALIDATED";

    private final KeycloakSession session;
    private final RoleModel roleForValidated;
    private final RoleModel roleForRejected;
    private final String messageForPending;
    private final String messageForRejected;
    private final Boolean automaticallyAddRequiredActionIfNotValidatedUser;

    public UserValidatedRequiredActionProvider(KeycloakSession session, String roleForValidated, String roleForRejected, 
                                               String messageForPending, String messageForRejected, Boolean automaticallyAddRequiredActionIfNotValidatedUser){
        this.session = session;
        this.roleForValidated = session.getContext().getRealm().getRole(roleForValidated);
        this.roleForRejected = session.getContext().getRealm().getRole(roleForRejected);
        this.messageForPending = messageForPending;
        this.messageForRejected = messageForRejected;
        this.automaticallyAddRequiredActionIfNotValidatedUser = automaticallyAddRequiredActionIfNotValidatedUser;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // Called every time a user authenticates.
        if (!this.automaticallyAddRequiredActionIfNotValidatedUser) return;

        // Automatically add this required action to the user if not validated.
        if (context.getUser().hasRole(this.roleForRejected) || !context.getUser().hasRole(this.roleForValidated)) {
            LOG.infof("Unvalidated user: the user has the role '%s' or not has the role '%s', adding required action '%s'.", 
                      this.roleForRejected.getName(), this.roleForValidated.getName(), PROVIDER_ID);
            context.getUser().addRequiredAction(PROVIDER_ID);
            context.getAuthenticationSession().addRequiredAction(PROVIDER_ID);
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        if (context.getUser().hasRole(this.roleForRejected)) {
            LOG.infof("Rejected user: showing error message.");
            context.challenge(context.form()
                .setError(this.messageForRejected)
                .createErrorPage(Response.Status.UNAUTHORIZED));
        } else if (context.getUser().hasRole(this.roleForValidated)) {
            LOG.infof("Already validated user: removing required action '%s'.", PROVIDER_ID);
            context.getUser().removeRequiredAction(PROVIDER_ID);
            context.getAuthenticationSession().removeRequiredAction(PROVIDER_ID);
            context.success();
        } else {
            LOG.infof("User pending for validation: showing message.");
            context.challenge(context.form()
                //.setMessage(MessageType.INFO, messageForPending)
                .setAttribute("messageHeader", "Please be patient...")
                .setInfo(messageForPending)
                //.setError(this.messageForPending)
                .setStatus(Response.Status.UNAUTHORIZED)
                .createInfoPage());
            //context.challenge(context.form().createForm("terms.ftl"));  //.setInfo(this.messageForPending, null)            
            //context.failure();
        }
    }

    @Override
    public void processAction(RequiredActionContext context) {
        // Called when a required action has form input you want to process.
        if (context.getUser().hasRole(this.roleForRejected)) {
            context.failure();
        } else if (context.getUser().hasRole(this.roleForValidated)){
            context.success();
        } else context.failure();

        // // You can show the form again
        // context.challenge(context.form().createInfoPage());
    }
    
    @Override
    public void close() {
        // Nothing to do here
    }
}
