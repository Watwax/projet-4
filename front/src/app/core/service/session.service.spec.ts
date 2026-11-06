import { TestBed } from '@angular/core/testing';
import { SessionInformation } from '../models/sessionInformation.interface';
import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  const user: SessionInformation = {
    token: 'token',
    type: 'Bearer',
    id: 7,
    username: 'alex@example.com',
    firstName: 'Alex',
    lastName: 'Doe',
    admin: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('starts logged out and emits the initial state', () => {
    const states: boolean[] = [];

    service.$isLogged().subscribe(state => states.push(state));

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    expect(states).toEqual([false]);
  });

  it('stores the user and publishes the logged-in state', () => {
    const states: boolean[] = [];
    service.$isLogged().subscribe(state => states.push(state));

    service.logIn(user);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(user);
    expect(states).toEqual([false, true]);
  });

  it('clears the user and publishes the logged-out state', () => {
    const states: boolean[] = [];
    service.$isLogged().subscribe(state => states.push(state));
    service.logIn(user);

    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    expect(states).toEqual([false, true, false]);
  });
});
