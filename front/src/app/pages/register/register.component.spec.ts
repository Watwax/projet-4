import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../core/service/auth.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent integration', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: Router;

  const authService = { register: jest.fn() };

  beforeEach(async () => {
    authService.register.mockReset();
    await TestBed.configureTestingModule({
      imports: [RegisterComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('creates a form whose required fields prevent submission', () => {
    expect(component).toBeTruthy();
    expect(component.form.invalid).toBe(true);
    expect(fixture.nativeElement.querySelector('button[type="submit"]').disabled).toBe(true);
  });

  it('registers a valid user and routes to login', () => {
    authService.register.mockReturnValue(of(undefined));
    component.form.setValue({
      firstName: 'Alex',
      lastName: 'Doe',
      email: 'alex@example.com',
      password: 'pass123',
    });

    expect(component.form.valid).toBe(true);
    component.submit();

    expect(authService.register).toHaveBeenCalledWith({
      firstName: 'Alex',
      lastName: 'Doe',
      email: 'alex@example.com',
      password: 'pass123',
    });
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('displays an error when registration fails', () => {
    authService.register.mockReturnValue(throwError(() => new Error('Email already exists')));
    component.form.setValue({
      firstName: 'Alex',
      lastName: 'Doe',
      email: 'alex@example.com',
      password: 'pass123',
    });

    component.submit();
    fixture.detectChanges();

    expect(component.onError).toBe(true);
    expect(fixture.nativeElement.querySelector('.error').textContent).toContain('An error occurred');
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
