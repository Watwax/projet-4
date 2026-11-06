import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { SessionService } from '../core/service/session.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  const router = { navigate: jest.fn() };
  const sessionService = { isLogged: false };

  beforeEach(() => {
    router.navigate.mockReset();
    sessionService.isLogged = false;
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: router },
        { provide: SessionService, useValue: sessionService },
      ],
    });
    guard = TestBed.inject(AuthGuard);
  });

  it('allows an authenticated user to activate a protected route', () => {
    sessionService.isLogged = true;

    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('redirects an anonymous user to login', () => {
    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['login']);
  });
});
