import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Session } from '../../../../core/models/session.interface';
import { Teacher } from '../../../../core/models/teacher.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { DetailComponent } from './detail.component';

describe('DetailComponent integration', () => {
  let fixture: ComponentFixture<DetailComponent>;
  let component: DetailComponent;
  let router: Router;

  const session: Session = {
    id: 5,
    name: 'Evening yoga',
    description: 'Relax and stretch',
    date: new Date('2025-03-01'),
    teacher_id: 2,
    users: [7],
  };
  const teacher: Teacher = {
    id: 2,
    firstName: 'Taylor',
    lastName: 'Smith',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-02'),
  };
  const sessionApiService = {
    detail: jest.fn(),
    delete: jest.fn(),
    participate: jest.fn(),
    unParticipate: jest.fn(),
  };
  const teacherService = { detail: jest.fn() };
  const sessionService = {
    sessionInformation: {
      token: 'token',
      type: 'Bearer',
      id: 7,
      username: 'alex@example.com',
      firstName: 'Alex',
      lastName: 'Doe',
      admin: false,
    },
  };
  const snackBar = { open: jest.fn() };

  const createFixture = (): void => {
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    sessionApiService.detail.mockReset().mockReturnValue(of(session));
    sessionApiService.delete.mockReset().mockReturnValue(of(undefined));
    sessionApiService.participate.mockReset().mockReturnValue(of(undefined));
    sessionApiService.unParticipate.mockReset().mockReturnValue(of(undefined));
    teacherService.detail.mockReset().mockReturnValue(of(teacher));
    snackBar.open.mockReset();
    sessionService.sessionInformation.admin = false;
    await TestBed.configureTestingModule({
      imports: [DetailComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '5' } } },
        },
        { provide: SessionApiService, useValue: sessionApiService },
        { provide: SessionService, useValue: sessionService },
        { provide: TeacherService, useValue: teacherService },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    });
    TestBed.overrideComponent(DetailComponent, {
      add: { providers: [{ provide: MatSnackBar, useValue: snackBar }] },
    });
    await TestBed.compileComponents();
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  it('loads the session and teacher and shows participation status', () => {
    createFixture();

    expect(component.sessionId).toBe('5');
    expect(component.userId).toBe('7');
    expect(component.session).toEqual(session);
    expect(component.teacher).toEqual(teacher);
    expect(component.isParticipate).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('Evening Yoga');
    expect(fixture.nativeElement.textContent).toContain('Taylor SMITH');
    expect(fixture.nativeElement.textContent).toContain('Do not participate');
  });

  it('subscribes a user who is not yet participating and refreshes the session', () => {
    sessionApiService.detail.mockReturnValueOnce(of({ ...session, users: [] })).mockReturnValue(of(session));
    createFixture();

    expect(component.isParticipate).toBe(false);
    component.participate();

    expect(sessionApiService.participate).toHaveBeenCalledWith('5', '7');
    expect(sessionApiService.detail).toHaveBeenCalledTimes(2);
  });

  it('removes a participant and refreshes the session', () => {
    createFixture();

    component.unParticipate();

    expect(sessionApiService.unParticipate).toHaveBeenCalledWith('5', '7');
    expect(sessionApiService.detail).toHaveBeenCalledTimes(2);
  });

  it('deletes the session and displays confirmation for an administrator', () => {
    sessionService.sessionInformation.admin = true;
    createFixture();
    fixture.nativeElement.querySelector('button[color="warn"]').click();

    expect(sessionApiService.delete).toHaveBeenCalledWith('5');
    expect(snackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
