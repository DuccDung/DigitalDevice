using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class DeviceFunctionality
{
    public string FunctionId { get; set; } = null!;

    public string? Description { get; set; }

    public virtual ICollection<DeviceFunction> DeviceFunctions { get; set; } = new List<DeviceFunction>();
}
