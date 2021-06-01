import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchInputComponent } from './branch-input.component';

describe('BranchInputComponent', () => {
  let component: BranchInputComponent;
  let fixture: ComponentFixture<BranchInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BranchInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
