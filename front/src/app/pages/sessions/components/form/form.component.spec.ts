import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { FormComponent } from './form.component';

describe('FormComponent integration', () => {
  let router: Router;
  let currentUrl: string;
  let fixture: ComponentFixture<FormComponent>;
  let component: FormComponent;

  const session: Session = {
    id: 5,
    name: 'Evening yoga',
    description: 'Relax and stretch',
    date: new Date('2025-03-01T00:00:00.000Z'),
    teacher_id: 2,
    users: [],
  };
  const sessionApiService = {
    detail: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
  };
  const teacherService = { all: jest.fn() };
  const sessionService = { sessionInformation: { admin: true, id: 1 } };
  const snackBar = { open: jest.fn() };

  const createFixture = (): void => {
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    currentUrl = '/sessions/create';
    sessionApiService.detail.mockReset().mockReturnValue(of(session));
    sessionApiService.create.mockReset().mockReturnValue(of(session));
    sessionApiService.update.mockReset().mockReturnValue(of(session));
    teacherService.all.mockReset().mockReturnValue(of([{ id: 2, firstName: 'Taylor', lastName: 'Smith' }]));
    snackBar.open.mockReset();
    await TestBed.configureTestingModule({
      imports: [FormComponent, NoopAnimationsModule, RouterTestingModule],
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
    TestBed.overrideComponent(FormComponent, {
      add: { providers: [{ provide: MatSnackBar, useValue: snackBar }] },
    });
    await TestBed.compileComponents();
    router = TestBed.inject(Router);
    Object.defineProperty(router, 'url', {
      configurable: true,
      get: () => currentUrl,
    });
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  it('initializes an empty form for creating a session and prevents invalid submission', () => {
    createFixture();

    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm?.invalid).toBe(true);
    component.submit();

    expect(sessionApiService.create).not.toHaveBeenCalled();
  });

  it('creates a valid session and returns to the list', () => {
    createFixture();
    component.sessionForm?.setValue({
      name: 'Evening yoga',
      date: '2025-03-01',
      teacher_id: '2',
      description: 'Relax and stretch',
    });

    component.submit();

    expect(sessionApiService.create).toHaveBeenCalledWith({
      name: 'Evening yoga',
      date: '2025-03-01',
      teacher_id: 2,
      description: 'Relax and stretch',
    });
    expect(snackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('loads a session and updates it with normalized form data', () => {
    currentUrl = '/sessions/update/5';
    createFixture();

    expect(component.onUpdate).toBe(true);
    expect(sessionApiService.detail).toHaveBeenCalledWith('5');
    expect(component.sessionForm?.getRawValue()).toEqual({
      name: 'Evening yoga',
      date: '2025-03-01',
      teacher_id: 2,
      description: 'Relax and stretch',
    });

    component.sessionForm?.controls.name.setValue('Updated yoga');
    component.submit();

    expect(sessionApiService.update).toHaveBeenCalledWith('5', {
      name: 'Updated yoga',
      date: '2025-03-01',
      teacher_id: 2,
      description: 'Relax and stretch',
    });
    expect(snackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
  });

  it('redirects non-administrators away from the session form', () => {
    sessionService.sessionInformation.admin = false;
    createFixture();

    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.sessionForm).toBeDefined();
    sessionService.sessionInformation.admin = true;
  });
});
