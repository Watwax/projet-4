import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { User } from '../../core/models/user.interface';
import { SessionService } from '../../core/service/session.service';
import { UserService } from '../../core/service/user.service';
import { MeComponent } from './me.component';

describe('MeComponent integration', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let router: Router;

  const user: User = {
    id: 7,
    email: 'alex@example.com',
    firstName: 'Alex',
    lastName: 'Doe',
    admin: false,
    password: 'not-used-in-view',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-02'),
  };
  const userService = { getById: jest.fn(), delete: jest.fn() };
  const sessionService = { sessionInformation: { id: 7 }, logOut: jest.fn() };
  const snackBar = { open: jest.fn() };

  beforeEach(async () => {
    userService.getById.mockReset().mockReturnValue(of(user));
    userService.delete.mockReset().mockReturnValue(of(undefined));
    sessionService.logOut.mockReset();
    snackBar.open.mockReset();
    await TestBed.configureTestingModule({
      imports: [MeComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: sessionService },
        { provide: UserService, useValue: userService },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    });
    TestBed.overrideComponent(MeComponent, {
      add: { providers: [{ provide: MatSnackBar, useValue: snackBar }] },
    });
    await TestBed.compileComponents();
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads and displays the account details for a regular user', () => {
    expect(userService.getById).toHaveBeenCalledWith('7');
    expect(component.user).toEqual(user);
    expect(fixture.nativeElement.textContent).toContain('Alex DOE');
    expect(fixture.nativeElement.textContent).toContain('alex@example.com');
    expect(fixture.nativeElement.textContent).toContain('Delete my account');
  });

  it('deletes the account, logs out and navigates to the home page', () => {
    component.delete();

    expect(userService.delete).toHaveBeenCalledWith('7');
    expect(snackBar.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 },
    );
    expect(sessionService.logOut).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('does not show account deletion for an administrator', () => {
    component.user = { ...user, admin: true };
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('You are admin');
    expect(fixture.nativeElement.textContent).not.toContain('Delete my account');
  });
});
