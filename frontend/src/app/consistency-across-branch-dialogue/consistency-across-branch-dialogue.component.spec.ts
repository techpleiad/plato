import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsistencyAcrossBranchDialogueComponent } from './consistency-across-branch-dialogue.component';

describe('ConsistencyAcrossBranchDialogueComponent', () => {
  let component: ConsistencyAcrossBranchDialogueComponent;
  let fixture: ComponentFixture<ConsistencyAcrossBranchDialogueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConsistencyAcrossBranchDialogueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsistencyAcrossBranchDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
