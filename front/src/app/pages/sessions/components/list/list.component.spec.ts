import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { ListComponent } from './list.component';

describe('ListComponent integration', () => {
  let fixture: ComponentFixture<ListComponent>;
  let component: ListComponent;

  const sessions: Session[] = [{
    id: 3,
    name: 'Morning yoga',
    description: 'A gentle session',
    date: new Date('2025-03-01'),
    teacher_id: 2,
    users: [7],
  }];
  const sessionApiService = { all: jest.fn() };
  const sessionService = {
    sessionInformation: {
      token: 'token',
      type: 'Bearer',
      id: 7,
      username: 'alex@example.com',
      firstName: 'Alex',
      lastName: 'Doe',
      admin: true,
    },
  };

  beforeEach(async () => {
    sessionApiService.all.mockReset().mockReturnValue(of(sessions));
    await TestBed.configureTestingModule({
      imports: [ListComponent, NoopAnimationsModule, RouterTestingModule],
      providers: [
        { provide: SessionApiService, useValue: sessionApiService },
        { provide: SessionService, useValue: sessionService },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads and displays sessions and admin actions', () => {
    expect(sessionApiService.all).toHaveBeenCalledTimes(1);
    expect(component.user).toEqual(sessionService.sessionInformation);
    expect(fixture.nativeElement.textContent).toContain('Morning yoga');
    expect(fixture.nativeElement.textContent).toContain('A gentle session');
    expect(fixture.nativeElement.textContent).toContain('Create');
    expect(fixture.nativeElement.textContent).toContain('Edit');
  });

  it('hides admin actions for a regular user', () => {
    sessionService.sessionInformation.admin = false;
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('Create');
    expect(fixture.nativeElement.textContent).not.toContain('Edit');
    expect(fixture.nativeElement.textContent).toContain('Detail');
  });
});
