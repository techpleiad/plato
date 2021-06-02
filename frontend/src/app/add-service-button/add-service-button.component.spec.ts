import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddServiceButtonComponent } from './add-service-button.component';

describe('AddServiceButtonComponent', () => {
  let component: AddServiceButtonComponent;
  let fixture: ComponentFixture<AddServiceButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddServiceButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddServiceButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
