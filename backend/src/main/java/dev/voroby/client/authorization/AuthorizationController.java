package dev.voroby.client.authorization;

import dev.voroby.springframework.telegram.client.updates.ClientAuthorizationState;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/authorization")
public class AuthorizationController {

    private final ClientAuthorizationState authorizationState;

    public AuthorizationController(ClientAuthorizationState authorizationState) {
        this.authorizationState = authorizationState;
    }

    record Credential(String value){}

    @PostMapping(value = "/code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateConfirmationCode(@RequestBody Credential credential) {
        authorizationState.checkAuthenticationCode(credential.value);
    }

    @PostMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updatePassword(@RequestBody Credential credential) {
        authorizationState.checkAuthenticationPassword(credential.value);
    }

    @GetMapping(value = "/waitCode")
    public boolean waitCode() {
        return authorizationState.isWaitAuthenticationCode();
    }

    @GetMapping(value = "/waitPass")
    public boolean waitPass() {
        return authorizationState.isWaitAuthenticationPassword();
    }

    @GetMapping(value = "/status")
    public String authorizationStatus() {
        return authorizationState.haveAuthorization() ? "AUTHORIZED" : "NOT_AUTHORIZED";
    }

}
