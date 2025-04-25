package grycap.keycloak.requiredaction.uservalidated;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

public class UserValidatedRequiredActionFactory implements RequiredActionFactory, ServerInfoAwareProviderFactory {
    private static final Logger LOG = Logger.getLogger(UserValidatedRequiredActionFactory.class);

    private String roleForValidated;
    private String roleForRejected;
    private String messageForPending;
    private String messageForRejected;
    private Boolean automaticallyAddRequiredActionIfNotValidatedUser;

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return new UserValidatedRequiredActionProvider(session, this.roleForValidated, this.roleForRejected, this.messageForPending, this.messageForRejected,
                                                       this.automaticallyAddRequiredActionIfNotValidatedUser);
    }

    @Override
    public void init(Config.Scope config) {
        LOG.info( String.format( "### ------------  %s.init() ------------ ###", this.getId() ) );
        
        this.roleForValidated = config.get("roleForValidated", "validated_user");
        this.roleForRejected = config.get("roleForRejected", "rejected_user");
        this.messageForPending = config.get("messageForPending", "The account is pendig for validation.");
        this.messageForRejected = config.get("messageForRejected", "This user account has not been accepted.");
        String autoAdd = config.get("automaticallyAddRequiredActionIfNotValidatedUser", "NO");
        this.automaticallyAddRequiredActionIfNotValidatedUser = (autoAdd.equalsIgnoreCase("yes") || autoAdd.equalsIgnoreCase("true"));
        
        LOG.info ("User validated required action configuration variables: ");
        LOG.info ("\tRole for validated: " + this.roleForValidated);
        LOG.info ("\tRole for rejected: " + this.roleForRejected);
        LOG.info ("\tMessage for pending: " + this.messageForPending);
        LOG.info ("\tMessage for rejected: " + this.messageForRejected);
        LOG.info ("\tAutomatically add required action if not validated user: " + this.automaticallyAddRequiredActionIfNotValidatedUser);
        LOG.info( String.format("-----------------------------------------------------------") );
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do here
    }

    @Override
    public void close() {
        // Nothing to do here
    }

    @Override
    public String getId() {
        return UserValidatedRequiredActionProvider.PROVIDER_ID;
    }

    @Override
    public String getDisplayText() {
        return "User Must Be Validated";
    }
    
    @Override
    public Map<String, String> getOperationalInfo() {
        /**
         * This method is used to show info from this provider to the Keycloak admin user in the Server Info page
         * in th Keycloak Admin Console.
         */
        Map<String, String> ret = new LinkedHashMap<>();
        ret.put("roleForValidated", this.roleForValidated);
        ret.put("roleForRejected", this.roleForRejected);
        ret.put("messageForPending", this.messageForPending);
        ret.put("messageForRejected", this.messageForRejected);
        ret.put("automaticallyAddRequiredActionIfNotValidatedUser", this.automaticallyAddRequiredActionIfNotValidatedUser.toString());
        return ret;
    }
}
