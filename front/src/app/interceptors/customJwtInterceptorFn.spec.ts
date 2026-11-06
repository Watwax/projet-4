import { HttpHandlerFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { SessionService } from '../core/service/session.service';
import { customJwtInterceptorFn } from './customJwtInterceptorFn';

describe('customJwtInterceptorFn integration', () => {
  let sessionService: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [SessionService] });
    sessionService = TestBed.inject(SessionService);
  });

  it('forwards anonymous requests without an authorization header', () => {
    const request = new HttpRequest('GET', '/api/session');
    let forwardedRequest: HttpRequest<unknown> | undefined;
    const next: HttpHandlerFn = outgoingRequest => {
      forwardedRequest = outgoingRequest;
      return of(new HttpResponse<unknown>());
    };

    TestBed.runInInjectionContext(() => customJwtInterceptorFn(request, next));

    expect(forwardedRequest).toBe(request);
    expect(forwardedRequest?.headers.has('Authorization')).toBe(false);
  });

  it('adds authorization to requests made during an authenticated session', () => {
    sessionService.logIn({
      token: 'test-token',
      type: 'Bearer',
      id: 7,
      username: 'alex@example.com',
      firstName: 'Alex',
      lastName: 'Doe',
      admin: false,
    });
    const request = new HttpRequest('GET', '/api/session');
    let forwardedRequest: HttpRequest<unknown> | undefined;
    const next: HttpHandlerFn = outgoingRequest => {
      forwardedRequest = outgoingRequest;
      return of(new HttpResponse<unknown>());
    };

    TestBed.runInInjectionContext(() => customJwtInterceptorFn(request, next));

    expect(forwardedRequest).not.toBe(request);
    expect(forwardedRequest?.headers.has('Authorization')).toBe(true);
    expect(forwardedRequest?.headers.get('Authorization')).toContain('test-token');
  });
});
