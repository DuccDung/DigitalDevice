using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class CategoryDevice
{
    public string CategoryDeviceId { get; set; } = null!;

    public string? Description { get; set; }

    public string? PhotoPath { get; set; }

    public virtual ICollection<Device> Devices { get; set; } = new List<Device>();
}
