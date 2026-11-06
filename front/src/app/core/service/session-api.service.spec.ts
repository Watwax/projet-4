import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Session } from '../models/session.interface';
import { SessionRequest } from '../models/sessionRequest.interface';
import { SessionApiService } from './session-api.service';

describe('SessionApiService integration', () => {
  let service: SessionApiService;
  let httpTestingController: HttpTestingController;

  const session: Session = {
    id: 3,
    name: 'Morning yoga',
    description: 'A gentle session',
    date: new Date('2025-03-01'),
    teacher_id: 2,
    users: [7],
  };
  const sessionRequest: SessionRequest = {
    name: session.name,
    description: session.description,
    date: session.date.toISOString(),
    teacher_id: session.teacher_id,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SessionApiService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('loads all sessions', () => {
    service.all().subscribe(response => expect(response).toEqual([session]));

    const request = httpTestingController.expectOne('api/session');
    expect(request.request.method).toBe('GET');
    request.flush([session]);
  });

  it('loads a session by id', () => {
    service.detail('3').subscribe(response => expect(response).toEqual(session));

    const request = httpTestingController.expectOne('api/session/3');
    expect(request.request.method).toBe('GET');
    request.flush(session);
  });

  it('deletes a session by id', () => {
    service.delete('3').subscribe(response => expect(response).toBeNull());

    const request = httpTestingController.expectOne('api/session/3');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });

  it('creates a session with the supplied request', () => {
    service.create(sessionRequest).subscribe(response => expect(response).toEqual(session));

    const request = httpTestingController.expectOne('api/session');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(sessionRequest);
    request.flush(session);
  });

  it('updates a session with the supplied request', () => {
    service.update('3', sessionRequest).subscribe(response => expect(response).toEqual(session));

    const request = httpTestingController.expectOne('api/session/3');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(sessionRequest);
    request.flush(session);
  });

  it('adds a participant to a session', () => {
    service.participate('3', '7').subscribe(response => expect(response).toBeNull());

    const request = httpTestingController.expectOne('api/session/3/participate/7');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toBeNull();
    request.flush(null);
  });

  it('removes a participant from a session', () => {
    service.unParticipate('3', '7').subscribe(response => expect(response).toBeNull());

    const request = httpTestingController.expectOne('api/session/3/participate/7');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
