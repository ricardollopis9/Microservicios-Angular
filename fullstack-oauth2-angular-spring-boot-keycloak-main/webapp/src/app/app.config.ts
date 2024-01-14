import { APP_INITIALIZER, ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {
  AuthConfig,
  OAuthService,
  provideOAuthClient,
} from 'angular-oauth2-oidc';
import { provideHttpClient } from '@angular/common/http';

export const authCodeFlowConfig: AuthConfig = {
  issuer: 'http://localhost:8181/realms/microservices-realm',
  tokenEndpoint:
    'http://localhost:8181/realms/microservices-realm/protocol/openid-connect/token',
  redirectUri: window.location.origin,
  clientId: 'microservices-client',
  responseType: 'code',
  scope: 'openid profile email',
  showDebugInformation: true,
};

function initializeOAuth(oauthService: OAuthService): Promise<void> {
  return new Promise((resolve) => {
    oauthService.configure(authCodeFlowConfig);
    oauthService.setupAutomaticSilentRefresh();
    oauthService.loadDiscoveryDocumentAndLogin().then(() => resolve());
  });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideOAuthClient(),
    {
      provide: APP_INITIALIZER,
      useFactory: (oauthService: OAuthService) => {
        return () => {
          initializeOAuth(oauthService);
        };
      },
      multi: true,
      deps: [OAuthService],
    },
  ],
};
