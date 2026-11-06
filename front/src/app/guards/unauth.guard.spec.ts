import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SessionService } from '../core/service/session.service';
import { UnauthGuard } from './unauth.guard';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  const router = { navigate: jest.fn() };
  const sessionService = { isLogged: false };

  beforeEach(() => {
    router.navigate.mockReset();
    sessionService.isLogged = false;
    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: Router, useValue: router },
        { provide: SessionService, useValue: sessionService },
      ],
    });
    guard = TestBed.inject(UnauthGuard);
  });

  it('allows an anonymous user to activate a public route', () => {
    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('redirects an authenticated user to the session list', () => {
    sessionService.isLogged = true;

    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
