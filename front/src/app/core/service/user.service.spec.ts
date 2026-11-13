import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { User } from '../models/user.interface';
import { UserService } from './user.service';

describe('UserService integration', () => {
  let service: UserService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(UserService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('loads the requested user', () => {
    const user: User = {
      id: 8,
      email: 'alex@example.com',
      firstName: 'Alex',
      lastName: 'Doe',
      admin: false,
      password: 'not-used-in-view',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-02'),
    };

    service.getById('8').subscribe(response => expect(response).toEqual(user));

    const request = httpTestingController.expectOne('api/user/8');
    expect(request.request.method).toBe('GET');
    request.flush(user);
  });

  it('deletes the requested user', () => {
    service.delete('8').subscribe(response => expect(response).toBeNull());

    const request = httpTestingController.expectOne('api/user/8');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
