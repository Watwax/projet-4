import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './core/service/auth.service';
import { SessionService } from './core/service/session.service';
import { AppComponent } from './app.component';

describe('AppComponent integration', () => {
  let fixture: ComponentFixture<AppComponent>;
  let router: Router;
  let sessionService: SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [{ provide: AuthService, useValue: {} }],
    }).compileComponents();
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
    sessionService = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
  });

  it('shows public navigation while logged out', () => {
    expect(fixture.nativeElement.textContent).toContain('Login');
    expect(fixture.nativeElement.textContent).toContain('Register');
    expect(fixture.nativeElement.textContent).not.toContain('Logout');
  });

  it('shows authenticated links and logs out to the root route', () => {
    sessionService.logIn({
      token: 'token',
      type: 'Bearer',
      id: 7,
      username: 'alex@example.com',
      firstName: 'Alex',
      lastName: 'Doe',
      admin: false,
    });
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Sessions');
    expect(fixture.nativeElement.textContent).toContain('Account');

    fixture.nativeElement.querySelector('.link:last-child').click();

    expect(sessionService.isLogged).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['']);
  });
});
