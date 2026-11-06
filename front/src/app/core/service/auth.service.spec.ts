import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoginRequest } from '../models/loginRequest.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';
import { AuthService } from './auth.service';

describe('AuthService integration', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('registers a user with the supplied request', () => {
    const requestBody: RegisterRequest = {
      email: 'alex@example.com',
      firstName: 'Alex',
      lastName: 'Doe',
      password: 'pass123',
    };

    service.register(requestBody).subscribe(response => expect(response).toBeNull());

    const request = httpTestingController.expectOne('/api/auth/register');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(requestBody);
    request.flush(null);
  });

  it('logs in and returns the session information', () => {
    const requestBody: LoginRequest = { email: 'alex@example.com', password: 'pass123' };
    const session: SessionInformation = {
      token: 'token',
      type: 'Bearer',
      id: 7,
      username: 'alex@example.com',
      firstName: 'Alex',
      lastName: 'Doe',
      admin: false,
    };

    service.login(requestBody).subscribe(response => expect(response).toEqual(session));

    const request = httpTestingController.expectOne('/api/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(requestBody);
    request.flush(session);
  });
});
