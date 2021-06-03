import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddSearchComponent } from './add-search.component';

describe('AddSearchComponent', () => {
  let component: AddSearchComponent;
  let fixture: ComponentFixture<AddSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddSearchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
