import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { SessionInformation } from '../../core/models/sessionInformation.interface';
import { AuthService } from '../../core/service/auth.service';
import { SessionService } from '../../core/service/session.service';
import { LoginComponent } from './login.component';

describe('LoginComponent integration', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;

  const session: SessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 7,
    username: 'alex@example.com',
    firstName: 'Alex',
    lastName: 'Doe',
    admin: false,
  };
  const authService = { login: jest.fn() };
  const sessionService = { logIn: jest.fn() };

  beforeEach(async () => {
    authService.login.mockReset();
    sessionService.logIn.mockReset();
    await TestBed.configureTestingModule({
      imports: [LoginComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: SessionService, useValue: sessionService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('creates the login form and disables submission until it is valid', () => {
    expect(component).toBeTruthy();
    expect(component.form.invalid).toBe(true);
    expect(fixture.nativeElement.querySelector('button[type="submit"]').disabled).toBe(true);
    expect(component.form.controls.email.hasError('required')).toBe(true);
  });

  it('rejects an invalid email and displays an authentication error', () => {
    component.form.setValue({ email: 'not-an-email', password: 'pass123' });
    expect(component.form.controls.email.hasError('email')).toBe(true);

    authService.login.mockReturnValue(throwError(() => new Error('Unauthorized')));
    component.form.setValue({ email: 'alex@example.com', password: 'pass123' });
    component.submit();
    fixture.detectChanges();

    expect(component.onError).toBe(true);
    expect(fixture.nativeElement.querySelector('.error').textContent).toContain('An error occurred');
    expect(sessionService.logIn).not.toHaveBeenCalled();
  });

  it('logs in, stores the session and navigates to sessions', () => {
    authService.login.mockReturnValue(of(session));
    component.form.setValue({ email: 'alex@example.com', password: 'pass123' });

    component.submit();

    expect(authService.login).toHaveBeenCalledWith({ email: 'alex@example.com', password: 'pass123' });
    expect(sessionService.logIn).toHaveBeenCalledWith(session);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });
});
